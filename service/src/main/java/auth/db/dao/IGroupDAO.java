package auth.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import auth.api.v1.pojo.Group;
import common.status.ISystemStatusComponent;

import java.util.List;

/**
 * Data Access Object for Forxy database to manipulate Clients.
 */
public interface IGroupDAO extends PagingAndSortingRepository<Group, String>, ISystemStatusComponent {

    Page<Group> findAll(final Pageable pageable, final Group filter);

    List<Group> findAll(List<String> groupCodes);
}

