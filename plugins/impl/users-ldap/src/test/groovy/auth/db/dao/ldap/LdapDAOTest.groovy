package auth.db.dao.ldap

import auth.db.dao.UserAndGroupDAOTest
import org.springframework.test.context.ContextConfiguration

/**
 * User Data Source tests for LDAP implementation
 */
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
class LdapDAOTest extends UserAndGroupDAOTest {
}
