package auth.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import common.status.ISystemStatusComponent;
import auth.api.v1.pojo.Client;

/**
 * Data Access Object for Forxy database to manipulate Clients.
 */
public interface IClientDAO extends PagingAndSortingRepository<Client, String>, ISystemStatusComponent {

    Page<Client> findAll(final Pageable pageable, final Client filter);
}

