package auth.db.dao.cassandra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import common.status.pojo.ComponentStatus;
import common.status.pojo.StatusType;
import auth.db.dao.IUserDAO;
import auth.db.dao.cassandra.client.ICassandraClient;
import auth.api.v1.pojo.User;

import java.util.Date;
import java.util.List;

/**
 * Cassandra DB based data source for users
 */
public class UserDAO implements IUserDAO {

    private ICassandraClient<User> cassandraClient;

    @Override
    public Page<User> findAll(Pageable pageable, User filter) {
        return findAll(pageable);
    }

    @Override
    public List<User> findByLastName(final String LastName) {
        return null;
    }

    @Override
    public List<User> findByFirstName(final String firstName) {
        return null;
    }

    @Override
    public Iterable<User> findAll(final Sort sort) {
        return cassandraClient.getAll();
    }

    @Override
    public Page<User> findAll(final Pageable pageable) {
        return new PageImpl<User>(cassandraClient.getAll());
    }

    @Override
    public <T extends User> T save(final T user) {
        cassandraClient.add(user);
        return user;
    }

    @Override
    public <T extends User> Iterable<T> save(final Iterable<T> entities) {
        for (final User user : entities) {
            cassandraClient.add(user);
        }
        return entities;
    }

    @Override
    public User findOne(final String s) {
        return cassandraClient.getByKey(s);
    }

    @Override
    public boolean exists(final String s) {
        return cassandraClient.existsKey(s);
    }

    @Override
    public Iterable<User> findAll() {
        return cassandraClient.getAll();
    }

    @Override
    public Iterable<User> findAll(final Iterable<String> strings) {
        return cassandraClient.getAll();
    }

    @Override
    public long count() {
        return cassandraClient.count();
    }

    @Override
    public void delete(final String s) {
        cassandraClient.deleteByKey(s);
    }

    @Override
    public void delete(final User user) {
        cassandraClient.delete(user);
    }

    @Override
    public void delete(final Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public ComponentStatus getStatus() {
        return new ComponentStatus("Cassandra", "localhost", StatusType.YELLOW, null, ComponentStatus.ComponentType.DB,
                0, new Date(), null, null);
    }

    public void setCassandraClient(final ICassandraClient<User> cassandraClient) {
        this.cassandraClient = cassandraClient;
    }
}
