package auth.db.dao.ldap

import auth.api.v1.Group
import auth.db.dao.BaseGroupDAO
import auth.db.event.GroupChanged
import auth.db.event.GroupRemoved
import auth.db.exceptions.AuthDBEvent
import common.exceptions.ServiceException
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.apache.commons.lang.exception.ExceptionUtils
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
 * LDAP Based Group Data Source
 */
class GroupDAO extends BaseGroupDAO {

    private static final String[] GROUP_CLASS_NAMES = ['groupOfNames', 'top']

    LdapTemplate ldapTemplate
    String adminLoginName
    String codePropertyName
    String namePropertyName
    String groupsUnitName
    String loginPropertyName

    LdapName getBaseDN() {
        ldapTemplate.contextSource.readOnlyContext.environment.get('org.springframework.ldap.base.path') as LdapName
    }

    LdapName getUserDN(String login) {
        LdapUtils.prepend(
                ldapTemplate.searchForContext(
                        query().where('objectClass').is('person').and(loginPropertyName).is(login)
                ).dn,
                baseDN
        )
    }

    String extractLoginFromDN(String dn) {
        LdapUtils.getStringValue(LdapUtils.newLdapName(dn), loginPropertyName)
    }

    protected Name buildDn(String code) {
        return LdapNameBuilder.newInstance()
                .add('ou', groupsUnitName)
                .add(codePropertyName, code)
                .build()
    }

    private final ContextMapper<Group> groupContextMapper = new AbstractContextMapper<Group>() {
        @Override
        public Group doMapFromContext(DirContextOperations context) {
            new Group(
                    code: context.getStringAttribute(codePropertyName),
                    name: context.getStringAttribute(namePropertyName),
                    description: context.getStringAttribute('description'),
                    members: context.getAttributeSortedStringSet('member')?.collect {
                        extractLoginFromDN it
                    }
            )
        }
    }

    protected void mapToContext(Group group, DirContextOperations context) {
        context.setAttributeValues('objectClass', GROUP_CLASS_NAMES)
        context.setAttributeValue(codePropertyName, group.code)
        context.setAttributeValue(namePropertyName, group.name)
        context.setAttributeValue('description', group.description)
        context.setAttributeValues('member', group.members?.collect { getUserDN it }?.toArray())
    }

    @Override
    List<Group> findAll() {
        return ldapTemplate.search(query().where('objectClass').is('groupOfNames'), groupContextMapper)
    }

    @Override
    long count() {
        return findAll().size()
    }

    @Override
    List<Group> find(final Set<String> groupCodes) {
        return groupCodes?.collect { find it }
    }

    @Override
    Page<Group> find(final Pageable pageable, final Group filter) {
        List<Group> groups = ldapTemplate.search(
                query().where('objectClass').is('groupOfNames')
                        .and(codePropertyName).whitespaceWildcardsLike(filter.code)
                        .and(namePropertyName).whitespaceWildcardsLike(filter.name)
                        .and('description').whitespaceWildcardsLike(filter.description)
                , groupContextMapper
        )
        return new PageImpl<Group>(groups)
    }

    @Override
    List<Group> find(final Sort sort) {
        return ldapTemplate.search(query().where('objectClass').is('groupOfNames'), groupContextMapper)
    }

    @Override
    Page<Group> find(final Pageable pageable) {
        return new PageImpl<Group>(find((Sort) null))
    }

    Group create(Group group) {
        DirContextAdapter context = new DirContextAdapter(buildDn(group.code))
        group.members = group.members ? group.members + adminLoginName : [adminLoginName]
        mapToContext(group, context)
        ldapTemplate.bind(context)
        return group
    }

    @Override
    Group save(final Group group) {
        Group old = find group.code
        silentSave group
        eventBus.post(new GroupChanged(from: old, to: group))
        return group
    }

    @Override
    Group silentSave(final Group group) {
        Name dn = buildDn(group.code)
        DirContextOperations context = ldapTemplate.lookupContext(dn)
        mapToContext(group, context)
        ldapTemplate.modifyAttributes(context)
        return group
    }

    @Override
    Group find(final String code) {
        List<Group> result = ldapTemplate.search(
                query().where('objectClass').is('groupOfNames').and(codePropertyName).is(code),
                groupContextMapper)
        return result?.size() > 0 ? result.first() : null
    }

    @Override
    boolean exists(final String code) {
        return find(code)
    }

    @Override
    void delete(final String code) {
        Group group = find code
        if (!group) {
            throw new ServiceException(AuthDBEvent.GroupNotFound, code)
        }
        ldapTemplate.unbind(buildDn(code))
        eventBus.post(new GroupRemoved(group: group))
    }

    @Override
    void delete(final Group group) {
        delete group.code
    }

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        if (ldapTemplate?.contextSource?.readOnlyContext?.environment) {
            location = ldapTemplate.contextSource.readOnlyContext.environment.get('java.naming.provider.url')

            long timeStart = new Date().time
            try {
                ldapTemplate.lookup('')
            } catch (final Exception e) {
                exceptionMessage = e.message
                exceptionDetails = ExceptionUtils.getStackTrace(e)
                statusType = StatusType.RED
            } finally {
                responseTime = new Date().time - timeStart
            }
        } else {
            statusType = StatusType.RED
        }
        return new ComponentStatus(
                name: 'Group DAO',
                location: location,
                status: statusType,
                componentType: ComponentStatus.ComponentType.DB,
                responseTime: responseTime,
                exceptionMessage: exceptionMessage,
                exceptionDetails: exceptionDetails
        )
    }
}
