package auth.db.dao.cassandra

import auth.api.v1.User
import auth.db.dao.IUserDAO
import auth.db.dao.cassandra.client.ICassandraClient
import common.status.pojo.ComponentStatus
import common.status.pojo.StatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * Cassandra DB based data source for users
 */
class UserDAO implements IUserDAO {

    ICassandraClient<User> cassandraClient

    @Override
    Page<User> findAll(final Pageable pageable, final User filter) {
        return findAll(pageable)
    }

    @Override
    Iterable<User> findAll(final Sort sort) {
        return cassandraClient.getAll()
    }

    @Override
    Page<User> findAll(final Pageable pageable) {
        return new PageImpl<User>(cassandraClient.getAll())
    }

    @Override
    User save(final User user) {
        cassandraClient.add(user)
        return user
    }

    @Override
    User findOne(final String s) {
        return cassandraClient.getByKey(s)
    }

    @Override
    boolean exists(final String s) {
        return cassandraClient.existsKey(s)
    }

    @Override
    Iterable<User> findAll() {
        return cassandraClient.getAll()
    }

    @Override
    long count() {
        return cassandraClient.count()
    }

    @Override
    void delete(final String s) {
        cassandraClient.deleteByKey(s)
    }

    @Override
    void delete(final User user) {
        cassandraClient.delete(user)
    }

    @Override
    ComponentStatus getStatus() {
        return new ComponentStatus('Cassandra', 'localhost', StatusType.YELLOW, null, ComponentStatus.ComponentType.DB,
                0, new Date(), null, null)
    }
}
