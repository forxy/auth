package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class Token {
    String tokenKey
    String clientID
    String type
    String refreshToken
    String subject
    Long expiresIn
    DateTime issuedAt
    Set<String> scopes
}
