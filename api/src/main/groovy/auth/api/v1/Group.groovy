package auth.api.v1

import groovy.transform.Canonical
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'scopes_group')
class Group {

    @Id
    String code

    @Indexed(unique = true)
    String name

    String description

    Map<String, List<String>> scopes = new HashMap<>()

    DateTime updateDate

    String updatedBy

    DateTime createDate

    String createdBy
}
