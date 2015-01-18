package auth.db.dao.mongo

import auth.db.dao.ClientDAOTest
import org.springframework.test.context.ContextConfiguration

/**
 * Client Data Source tests for MongoDB implementation
 */
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
class MongoDAOTest extends ClientDAOTest {
}
