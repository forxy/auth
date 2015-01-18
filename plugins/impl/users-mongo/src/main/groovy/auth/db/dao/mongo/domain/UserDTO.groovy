package auth.db.dao.mongo.domain

import auth.api.v1.Gender
import auth.api.v1.User
import groovy.transform.Canonical
import org.codehaus.groovy.runtime.InvokerHelper
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'user')
class UserDTO {

    @Id
    String login
    @Indexed
    String email
    String password

    String firstName
    String lastName
    Gender gender
    DateTime birthDate

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy

    Set<String> groups

    public static UserDTO toDomain(User user) {
        UserDTO domain = new UserDTO()
        InvokerHelper.setProperties(domain, user?.properties)
        domain.groups = domain.groups ?: []
        return domain
    }

    public static User fromDomain(UserDTO domain) {
        User user = new User()
        InvokerHelper.setProperties(user, domain?.properties)
        user.groups = user.groups ?: []
        return user
    }

    public static List<User> fromDomain(List<UserDTO> domains) {
        domains?.collect {
            fromDomain(it)
        }
    }
}
