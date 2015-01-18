package auth.db.dao.mongo.domain

import auth.api.v1.Group
import groovy.transform.Canonical
import org.codehaus.groovy.runtime.InvokerHelper
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'user_group')
class GroupDTO {

    @Id
    String code
    String name
    String description
    Set<String> members

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy

    Map<String, List<String>> scopes = new HashMap<>()

    public static GroupDTO toDomain(Group group) {
        GroupDTO domain = new GroupDTO()
        InvokerHelper.setProperties(domain, group?.properties)
        domain.members = domain.members ?: []
        return domain
    }

    public static Group fromDomain(GroupDTO domain) {
        Group group = new Group()
        InvokerHelper.setProperties(group, domain?.properties)
        group.members = group.members ?: []
        return group
    }

    public static List<Group> fromDomain(List<GroupDTO> domains) {
        domains?.collect {
            fromDomain(it)
        }
    }
}
