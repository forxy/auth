package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class User extends Account {

    String login

    String firstName
    String lastName
    Gender gender
    DateTime birthDate

    String title
    Set<String> telephones
    Address address

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy

    Set<String> groups = []
}
