package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
class Client {

    String clientID
    String secret

    String name
    String description
    String webUri

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy

    List<String> redirectUris = new ArrayList<>()
    List<String> scopes = new ArrayList<>()
    List<String> audiences = new ArrayList<>()
}
