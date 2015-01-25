package auth.db.dao.ldap

import auth.api.v1.User
import auth.db.dao.BaseUserDAO
import auth.db.event.GroupRemoved
import auth.db.event.UserChanged
import auth.db.event.UserRemoved
import auth.db.exceptions.AuthDBEvent
import common.exceptions.ServiceException
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.apache.commons.lang.exception.ExceptionUtils
import org.joda.time.DateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.ldap.core.ContextMapper
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.AbstractContextMapper
import org.springframework.ldap.support.LdapNameBuilder
import org.springframework.ldap.support.LdapUtils

import javax.naming.Name
import javax.naming.ldap.LdapName

import static org.springframework.ldap.query.LdapQueryBuilder.query

/**
 * LDAP Based User Data Source
 */
class UserDAO extends BaseUserDAO {

    private static final String[] PERSON_CLASS_NAMES = ['person', 'organizationalPerson', 'inetOrgPerson', 'top']

    LdapTemplate ldapTemplate
    String loginPropertyName
    String groupCodePropertyName

    protected Name buildDn(String login) {
        return LdapNameBuilder.newInstance()
                .add(loginPropertyName, login)
                .build()
    }

    LdapName getBaseDN() {
        ldapTemplate.contextSource.readOnlyContext.environment.get('org.springframework.ldap.base.path') as LdapName
    }

    LdapName getUserDN(String id) {
        LdapUtils.prepend(
                ldapTemplate.searchForContext(
                        query().where('objectClass').is('person').and(
                                query().where('mail').is(id).or(
                                        query().where(loginPropertyName).is(id)
                                )
                        )
                ).dn,
                baseDN
        )
    }

    Set<String> getGroupCodesForUser(final String login) {
        ldapTemplate.search(
                query().where('objectClass').is('groupOfNames').and('member').is(getUserDN(login) as String),
                new AbstractContextMapper() {
                    @Override
                    protected String doMapFromContext(final DirContextOperations ctx) {
                        return ctx.getStringAttribute(groupCodePropertyName)
                    }
                }
        )
    }

    private final ContextMapper<User> userContextMapper = new AbstractContextMapper<User>() {
        @Override
        public User doMapFromContext(DirContextOperations context) {
            String login = context.getStringAttribute(loginPropertyName)
            new User(
                    email: context.getStringAttribute('mail'),
                    login: login,
                    firstName: context.getStringAttribute('givenName'),
                    lastName: context.getStringAttribute('sn'),
                    groups: getGroupCodesForUser(login)
            )
        }
    }

    private final ContextMapper<User> profileContextMapper = new AbstractContextMapper<User>() {
        @Override
        public User doMapFromContext(DirContextOperations context) {
            String login = context.getStringAttribute(loginPropertyName)
            new User(
                    email: context.getStringAttribute('mail'),
                    login: login,
                    firstName: context.getStringAttribute('givenName'),
                    lastName: context.getStringAttribute('sn'),
                    title: context.getStringAttribute('title'),
                    telephones: [context.getStringAttribute('telephoneNumber')],
                    groups: getGroupCodesForUser(login)
            )
        }
    }

    protected void mapToContext(User user, DirContextOperations context) {
        context.setAttributeValues('objectClass', PERSON_CLASS_NAMES)
        context.setAttributeValue(loginPropertyName, user.login)
        context.setAttributeValue('mail', user.email)
        context.setAttributeValue('givenName', user.firstName)
        context.setAttributeValue('sn', user.lastName)
        if (user.password) {
            context.setAttributeValue('userPassword', user.password)
        }
    }

    protected void mapProfileToContext(User user, DirContextOperations context) {
        context.setAttributeValues('objectClass', PERSON_CLASS_NAMES)
        context.setAttributeValue(loginPropertyName, user.login)
        context.setAttributeValue('mail', user.email)
        context.setAttributeValue('givenName', user.firstName)
        context.setAttributeValue('sn', user.lastName)
        if (user.password) {
            context.setAttributeValue('userPassword', user.password)
        }
        context.setAttributeValue('title', user.title)
        if (user.telephones) {
            context.setAttributeValue('telephoneNumber', user.telephones.toArray()[0])
        }
    }

    @Override
    List<User> findAll() {
        return ldapTemplate.search(query().where('objectClass').is('person'), userContextMapper)
    }

    @Override
    List<User> find(final Set<String> ids) {
        return ids?.collect { find it }
    }

    @Override
    Page<User> find(final Pageable pageable, final User filter) {
        List<User> users = ldapTemplate.search(
                query().where('objectClass').is('person')
                        .and('mail').whitespaceWildcardsLike(filter.email)
                        .and(loginPropertyName).whitespaceWildcardsLike(filter.login)
                        .and('givenName').whitespaceWildcardsLike(filter.firstName)
                        .and('sn').whitespaceWildcardsLike(filter.lastName)
                , userContextMapper
        )
        return new PageImpl<User>(users)
    }

    @Override
    List<User> find(final Sort sort) {
        return ldapTemplate.search(query().where('objectClass').is('person'), userContextMapper)
    }

    @Override
    Page<User> find(final Pageable pageable) {
        return new PageImpl<User>(find((Sort) null))
    }

    @Override
    User find(final String id) {
        List<User> result = ldapTemplate.search(
                query().where('objectClass').is('person').and(
                        query().where('mail').is(id).or(
                                query().where(loginPropertyName).is(id)
                        )
                ),
                userContextMapper)
        return result?.size() > 0 ? result.first() : null
    }

    @Override
    User getProfile(final String login) {
        List<User> result = ldapTemplate.search(
                query().where('objectClass').is('person').and(
                        query().where('mail').is(login).or(
                                query().where(loginPropertyName).is(login)
                        )
                ),
                profileContextMapper)
        return result?.size() > 0 ? result.first() : null
    }

    @Override
    boolean exists(final String id) {
        return find(id)
    }

    User create(User user) {
        DirContextAdapter context = new DirContextAdapter(buildDn(user.login))
        mapToContext(user, context)
        ldapTemplate.bind(context)
        mapNewGroups(user)
        return find(user.login)
    }

    @Override
    User save(final User user) {
        User old = find user.login
        silentSave user
        eventBus.post(new UserChanged(from: old, to: user))
        return user
    }

    @Override
    User silentSave(final User user) {
        DirContextOperations context = ldapTemplate.lookupContext(buildDn(user.login))
        mapToContext(user, context)
        ldapTemplate.modifyAttributes(context)
        return user
    }

    @Override
    User updateProfile(final User profile) {
        Name dn = buildDn(profile.login)
        DirContextOperations context = ldapTemplate.lookupContext(dn)
        mapProfileToContext(profile, context)
        ldapTemplate.modifyAttributes(context)
        User old = getProfile profile.login
        eventBus.post(new UserChanged(from: old, to: profile))
        return profile
    }

    @Override
    User authenticate(final String identifier, final String password) {
        try {
            ldapTemplate.authenticate(
                    query().where('objectClass').is('person')
                            .and(query().where('mail').is(identifier).or(loginPropertyName).is(identifier)),
                    password
            )
        } catch (Exception ignore) {
            return null
        }
        return find(identifier)
    }

    @Override
    long count() {
        return findAll().size()
    }

    @Override
    void delete(final String login) {
        User user = find login
        if (!user) {
            throw new ServiceException(AuthDBEvent.UserNotFound, login)
        }
        ldapTemplate.unbind(buildDn(login))
        eventBus.post(new UserRemoved(user: user))
    }

    @Override
    void delete(final User user) {
        delete user.login
    }

    @Override
    void updateGroupMembership(GroupRemoved groupRemoved) {}

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        if (ldapTemplate?.contextSource?.readOnlyContext?.environment) {
            location = ldapTemplate.contextSource.readOnlyContext.environment.get('java.naming.provider.url')

            long timeStart = DateTime.now().millis
            try {
                ldapTemplate.lookup('')
            } catch (final Exception e) {
                exceptionMessage = e.message
                exceptionDetails = ExceptionUtils.getStackTrace(e)
                statusType = StatusType.RED
            } finally {
                responseTime = DateTime.now().millis - timeStart
            }
        } else {
            statusType = StatusType.RED
        }
        return new ComponentStatus(
                name: 'User DAO',
                location: location,
                status: statusType,
                componentType: ComponentStatus.ComponentType.DB,
                responseTime: responseTime,
                exceptionMessage: exceptionMessage,
                exceptionDetails: exceptionDetails
        )
    }
}
