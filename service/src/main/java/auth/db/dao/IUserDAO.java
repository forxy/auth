package auth.db.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import common.status.ISystemStatusComponent;
import auth.api.v1.pojo.User;

import java.util.List;

/**
 * Data Access Object for User database to manipulate Users.
 */
public interface IUserDAO extends PagingAndSortingRepository<User, String>, ISystemStatusComponent {

    Page<User> findAll(final Pageable pageable, final User filter);

    List<User> findByLastName(final String LastName);

    List<User> findByFirstName(final String firstName);
}

