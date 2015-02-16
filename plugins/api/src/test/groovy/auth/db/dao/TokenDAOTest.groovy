package auth.db.dao

import spock.lang.Specification

import javax.annotation.Resource

/**
 * Token Data Source tests
 */
abstract class TokenDAOTest extends Specification {

    static final String TEST_EMAIL = 'test@email.com'
    static final String TEST_CLIENT_ID = UUID.randomUUID() as String

    @Resource
    ITokenDAO tokenDAO

    def "token DAO should be successfully initialized"() {
        expect:
        tokenDAO
    }
}
