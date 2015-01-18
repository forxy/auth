package auth.db.dao

import auth.api.v1.Client
import common.status.ISystemStatusComponent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Data Access Object for Forxy database to manipulate Clients.
 */
interface IClientDAO extends ISystemStatusComponent {

    List<Client> findAll()

    List<Client> find(final Set<String> clientIDs)

    Page<Client> find(final Pageable pageable, final Client filter)

    Page<Client> find(final Pageable pageable)

    List<Client> find(final Sort sort)

    Client find(final String s)

    Client authenticate(final String clientID, final String secret)

    Client create(Client client)

    Client save(final Client client)

    Client silentSave(final Client client)

    boolean exists(final String s)

    long count()

    void delete(final String s)

    void delete(final Client client)
}

