package auth.db.dao

import auth.api.v1.Group
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Data Access Object for Forxy database to manipulate Groups.
 */
interface IGroupDAO extends ISystemStatusComponent {

    List<Group> findAll()

    List<Group> find(final Set<String> groupCodes)

    Page<Group> find(final Pageable pageable, final Group filter)

    Page<Group> find(final Pageable pageable)

    List<Group> find(final Sort sort)

    Group create(Group group)

    Group save(final Group group)

    Group silentSave(final Group group)

    Group find(final String s)

    boolean exists(final String s)

    long count()

    void delete(final String s)

    void delete(final Group group)
}

