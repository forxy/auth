package auth.db.dao.redis

import auth.db.dao.TokenDAOTest
import org.springframework.test.context.ContextConfiguration

/**
 * Token Data Source tests for RedisDB implementation
 */
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
class RedisTokenDAOTest extends TokenDAOTest {
}
