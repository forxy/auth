package auth.db.dao.mongo

import auth.db.dao.UserAndGroupDAOTest
import org.springframework.test.context.ContextConfiguration

/**
 * User Data Source tests for MongoDB implementation
 */
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
class MongoUserDAOTest extends UserAndGroupDAOTest {
}
