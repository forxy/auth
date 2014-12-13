package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'token')
class Token {

    @Id
    String tokenKey

    @Indexed
    String clientID

    String type

    String refreshToken

    List<String> scopes

    UserSubject subject

    Long expiresIn

    @Indexed
    DateTime issuedAt
}
