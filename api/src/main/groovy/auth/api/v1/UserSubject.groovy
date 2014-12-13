package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class UserSubject {

    String userID

    List<String> roles

    Map<String, String> properties = new HashMap<String, String>()

    DateTime updateDate

    String updatedBy

    DateTime createDate

    String createdBy
}
