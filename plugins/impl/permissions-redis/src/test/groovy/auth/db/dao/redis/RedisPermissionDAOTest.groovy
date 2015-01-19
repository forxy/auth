package auth.db.dao.redis

import auth.db.dao.PermissionDAOTest
import org.springframework.test.context.ContextConfiguration

/**
 * Permissions Data Source tests for RedisDB implementation
 */
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
class RedisPermissionDAOTest extends PermissionDAOTest {
}
