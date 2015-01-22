package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class Client extends Account {

    String clientID

    String name
    String description
    String webUri

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy

    Set<String> redirectUris = []
    Set<String> scopes = []
    Set<String> audiences = []
}
