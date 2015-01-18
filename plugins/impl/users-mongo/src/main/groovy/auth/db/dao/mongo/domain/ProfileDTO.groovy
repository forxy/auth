package auth.db.dao.mongo.domain

import auth.api.v1.Address
import auth.api.v1.User
import groovy.transform.Canonical
import org.codehaus.groovy.runtime.InvokerHelper

@Canonical
class ProfileDTO extends UserDTO {

    String title
    Set<String> telephones
    Address address

    public static ProfileDTO toDomain(User user) {
        ProfileDTO domain = new ProfileDTO()
        InvokerHelper.setProperties(domain, user?.properties)
        domain.groups = domain.groups ?: []
        return domain
    }

    public static User fromDomain(ProfileDTO domain) {
        User profile = new User()
        InvokerHelper.setProperties(profile, domain?.properties)
        profile.groups = profile.groups ?: []
        return profile
    }

    public static List<User> fromDomain(List<ProfileDTO> domains) {
        domains?.collect {
            fromDomain(it)
        }
    }
}
