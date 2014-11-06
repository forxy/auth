package auth.db.dao;

import org.springframework.data.repository.CrudRepository;
import auth.api.v1.pojo.Profile;
import common.status.ISystemStatusComponent;

/**
 * Data Access Object for User database to manipulate Users.
 */
public interface IProfileDAO extends CrudRepository<Profile, String>, ISystemStatusComponent {
}

