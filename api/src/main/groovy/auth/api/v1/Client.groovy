package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'client')
class Client {

    @Id
    String clientID

    String secret

    @Indexed(unique = true)
    String name

    String description

    String webUri

    List<String> redirectUris = new ArrayList<>()

    List<String> scopes = new ArrayList<>()

    List<String> audiences = new ArrayList<>()

    DateTime updateDate

    String updatedBy

    DateTime createDate

    String createdBy
}
