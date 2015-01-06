package auth.db.dao.ldap

import auth.api.v1.Profile
import auth.api.v1.User
import auth.db.dao.IProfileDAO
import auth.db.dao.IUserDAO
import auth.exceptions.AuthEvent
import common.exceptions.ServiceException
import common.status.api.ComponentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.ldap.core.*
import org.springframework.ldap.core.support.AbstractContextMapper
import org.springframework.ldap.filter.EqualsFilter
import org.springframework.ldap.query.LdapQuery
import org.springframework.ldap.support.LdapNameBuilder
import org.springframework.ldap.support.LdapUtils

import javax.naming.Name
import javax.naming.NamingException
import javax.naming.directory.Attributes

import static org.springframework.ldap.query.LdapQueryBuilder.query

/**
 * LDAP Based User Data Source
 */
class UserDAO implements IUserDAO, IProfileDAO {

    LdapTemplate ldapTemplate

    @Override
    User authenticate(final String login, final String password) {
        ldapTemplate.authenticate(
                query().where('objectClass').is('person').and(
                        query().where('mail').is(login).or(
                                query().where('cn').is(login)
                        )
                ), password
        )
        return findOne(login)
    }

    @Override
    Page<User> findAll(final Pageable pageable, final User filter) {
        return null
    }

    @Override
    Iterable<User> findAll(final Sort sort) {
        EqualsFilter filter = new EqualsFilter('objectclass', 'person');
        return ldapTemplate.search(LdapUtils.emptyLdapName(), filter.encode(), contextMapper);
    }

    @Override
    Page<User> findAll(final Pageable pageable) {
        return null
    }

    @Override
    User save(final User user) {
        throw new ServiceException(AuthEvent.OperationNotAllowed)
    }

    @Override
    Profile save(final Profile user) {
        throw new ServiceException(AuthEvent.OperationNotAllowed)
    }

    @Override
    User findOne(final String login) {
        return ldapTemplate.search(
                query().where('objectClass').is('person').and(
                        query().where('mail').is(login).or(
                                query().where('cn').is(login)
                        )
                ),
                new UserAttributesMapper())?.first()
    }

    @Override
    Profile getProfile(final String login) {
        return ldapTemplate.search(
                query().where('objectClass').is('person').and(
                        query().where('mail').is(login).or(
                                query().where('cn').is(login)
                        )
                ),
                new ProfileAttributesMapper())?.first()
    }

    @Override
    boolean exists(final String s) {
        return false
    }

    @Override
    Iterable<User> findAll() {
        return null
    }

    @Override
    long count() {
        return 0
    }

    @Override
    void delete(final String login) {
        throw new ServiceException(AuthEvent.OperationNotAllowed)
    }

    @Override
    void delete(final User user) {
        ldapTemplate.unbind(buildDn(user));
    }

    @Override
    ComponentStatus getStatus() {
        return null
    }

    class UserAttributesMapper implements AttributesMapper<User> {
        @Override
        User mapFromAttributes(final Attributes attributes) throws NamingException {
            return new User(
                    email: attributes.get('mail')?.get(),
                    login: attributes.get('cn')?.get(),
                    lastName: attributes.get('sn')?.get()
            )
        }
    }

    class ProfileAttributesMapper implements AttributesMapper<Profile> {
        @Override
        Profile mapFromAttributes(final Attributes attributes) throws NamingException {
            return new Profile(
                    email: attributes.get('mail')?.get(),
                    login: attributes.get('cn')?.get(),
                    lastName: attributes.get('sn')?.get()
            )
        }
    }

    public void create(User person) {
        DirContextAdapter context = new DirContextAdapter(buildDn(person));
        mapToContext(person, context);
        ldapTemplate.bind(context);
    }

    public void update(User person) {
        Name dn = buildDn(person);
        DirContextOperations context = ldapTemplate.lookupContext(dn);
        mapToContext(person, context);
        ldapTemplate.modifyAttributes(context);
    }

    public User findByPrimaryKey(String name, String company, String country) {
        Name dn = buildDn(name, company, country);
        return ldapTemplate.lookup(dn, getContextMapper());
    }

    public List findByName(String name) {
        LdapQuery query = query()
                .where('objectclass').is('person')
                .and('cn').whitespaceWildcardsLike('name');

        return ldapTemplate.search(query, getContextMapper());
    }

    protected static ContextMapper getContextMapper() {
        return new UserContextMapper();
    }

    protected static Name buildDn(User user) {
        return buildDn(user.login, '', '');
    }

    protected static Name buildDn(String login, String company, String country) {
        return LdapNameBuilder.newInstance()
                .add('c', country)
                .add('ou', company)
                .add('cn', login)
                .build();
    }

    protected static void mapToContext(User person, DirContextOperations context) {
        context.setAttributeValues('objectclass', ['top', 'person']);
        context.setAttributeValue('cn', person.login);
        context.setAttributeValue('sn', person.lastName);
    }

    private static class UserContextMapper extends AbstractContextMapper<User> {
        public User doMapFromContext(DirContextOperations context) {
            return new User(
                    login: context.getStringAttribute('cn'),
                    email: context.getStringAttribute('mail'),
                    lastName: context.getStringAttribute('sn')
            );
        }
    }
}
