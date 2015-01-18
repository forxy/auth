package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class Token {
    String tokenKey
    String clientID
    String type
    String refreshToken
    UserSubject subject
    Long expiresIn
    DateTime issuedAt
    List<String> scopes
}
