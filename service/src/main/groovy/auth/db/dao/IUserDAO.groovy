package auth.db.dao

import auth.api.v1.pojo.User
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Data Access Object for User database to manipulate Users.
 */
interface IUserDAO extends ISystemStatusComponent {

    Page<User> findAll(final Pageable pageable, final User filter)

    Iterable<User> findAll(final Sort sort)

    Page<User> findAll(final Pageable pageable)

    User save(final User user)

    User findOne(final String s)

    boolean exists(final String s)

    Iterable<User> findAll()

    long count()

    void delete(final String s)

    void delete(final User user)
}

