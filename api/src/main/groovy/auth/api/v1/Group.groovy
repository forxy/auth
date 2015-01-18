package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime

@Canonical
class Group {

    String code
    String name
    String description
    Set<String> members

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy
}
