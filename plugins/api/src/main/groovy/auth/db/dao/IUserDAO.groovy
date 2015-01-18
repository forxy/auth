package auth.db.dao

import auth.api.v1.Group
import auth.api.v1.User
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Data Access Object for User database to manipulate Users.
 */
interface IUserDAO extends ISystemStatusComponent {

    List<User> findAll()

    List<User> find(Set<String> ids)

    Page<User> find(final Pageable pageable, final User filter)

    Page<User> find(final Pageable pageable)

    List<User> find(final Sort sort)

    User find(final String id)

    User getProfile(final String login)

    User authenticate(final String login, final String password)

    boolean exists(final String id)

    User create(final User user)

    User save(final User user)

    User silentSave(final User user)

    User updateProfile(final User profile)

    long count()

    void delete(final String id)

    void delete(final User user)
}

