package auth.db.dao

import auth.api.v1.pojo.Group
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Data Access Object for Forxy database to manipulate Groups.
 */
interface IGroupDAO extends ISystemStatusComponent {

    List<Group> findAll(final List<String> groupCodes)

    Page<Group> findAll(final Pageable pageable, final Group filter)

    Iterable<Group> findAll(final Sort sort)

    Page<Group> findAll(final Pageable pageable)

    Group save(final Group group)

    Group findOne(final String s)

    boolean exists(final String s)

    Iterable<Group> findAll()

    long count()

    void delete(final String s)

    void delete(final Group group)
}

