package auth.db.dao

import auth.api.v1.pojo.Client
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Data Access Object for Forxy database to manipulate Clients.
 */
interface IClientDAO extends ISystemStatusComponent {

    Page<Client> findAll(final Pageable pageable, final Client filter)

    Iterable<Client> findAll(final Sort sort)

    Page<Client> findAll(final Pageable pageable)

    Client save(final Client client)

    Client findOne(final String s)

    boolean exists(final String s)

    Iterable<Client> findAll()

    long count()

    void delete(final String s)

    void delete(final Client client)
}

