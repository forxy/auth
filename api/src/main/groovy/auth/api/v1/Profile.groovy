package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class Profile extends User {

    String companyName

    DateTime birthDate

    Set<String> telephones

    Address address
}
