package auth.test.dao

import auth.db.dao.DatabaseIntegrationTest
import org.springframework.test.context.ContextConfiguration

/**
 * Database integration test for the main authentication workflow
 */
@ContextConfiguration(locations = ['classpath:spring/spring-dao-integration-test-context.xml'])
class AuthDAOIntegrationTest extends DatabaseIntegrationTest {
}
