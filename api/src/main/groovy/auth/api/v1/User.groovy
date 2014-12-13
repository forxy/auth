package auth.api.v1

import groovy.transform.Canonical
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'user')
class User {

    @Id
    String email

    String password

    String login

    String firstName

    String lastName

    Gender gender

    List<String> groups

    Date updateDate = new Date()

    String updatedBy

    Date createDate = new Date()

    String createdBy
}
