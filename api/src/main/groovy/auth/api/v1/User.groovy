package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'user')
class User {

    @Id
    String email

    String password

    @Indexed
    String login

    String firstName

    String lastName

    Gender gender

    List<String> groups

    DateTime updateDate

    String updatedBy

    DateTime createDate

    String createdBy
}
