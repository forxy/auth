package auth.db.dao

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * Token Data Source tests
 */
abstract class TokenDAOTest extends AbstractJUnit4SpringContextTests {

    static final String TEST_EMAIL = 'test@email.com'
    static final String TEST_CLIENT_ID = UUID.randomUUID() as String

    @Autowired
    ITokenDAO tokenDAO

    @Test
    void testGrantAccessCode() {

    }
}
