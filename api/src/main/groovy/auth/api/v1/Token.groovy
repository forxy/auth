package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class Token {
    String accessToken
    String idToken
    String refreshToken
    Long expiresIn
    String tokenType
}
