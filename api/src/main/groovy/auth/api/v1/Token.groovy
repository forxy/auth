package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

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
