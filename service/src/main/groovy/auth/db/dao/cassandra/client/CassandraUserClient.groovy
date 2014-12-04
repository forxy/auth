package auth.db.dao.cassandra.client

import auth.api.v1.Gender
import auth.api.v1.User
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import org.springframework.beans.factory.InitializingBean

/**
 * Cassandra client implementation for User entity
 */
class CassandraUserClient implements ICassandraClient<User>, InitializingBean {

    PreparedStatement getByKeyStatement
    PreparedStatement getPageStatement
    PreparedStatement addStatement
    PreparedStatement deleteStatement

    Session session

    @Override
    List<User> getAll() {
        List<Row> rows = getSession().execute('select * from user').all()
        List<User> users = new ArrayList<User>(rows.size())
        for (final Row row : rows) {
            users.add(toUser(row))
        }
        return users
    }

    @Override
    List<User> getPage(final long start, final long size) {
        return null
    }

    @Override
    User getByKey(final String key) {
        return toUser(getSession().execute(new BoundStatement(getByKeyStatement).bind(key)).one())
    }

    @Override
    boolean existsKey(final String key) {
        return getSession().execute(new BoundStatement(getByKeyStatement).bind(key)).one() != null
    }

    @Override
    boolean exists(final User entity) {
        return getSession().execute(new BoundStatement(getByKeyStatement).bind(entity.getEmail())).one() != null
    }

    @Override
    void add(final User user) {
        final BoundStatement newUserStatement = new BoundStatement(addStatement)
        /*newUserStatement.setString('email', user.getEmail())
        newUserStatement.setBytes('password', ByteBuffer.wrap(user.getPassword()))
        newUserStatement.setString('login', user.getLogin())
        newUserStatement.setString('first_name', user.getFirstName())
        newUserStatement.setString('last_name', user.getLastName())
        newUserStatement.setString('gender', String.valueOf(user.getGender()))
        newUserStatement.setDate('birth_date', user.getBirthDate())*/
        // @formatter:off
        getSession().execute(newUserStatement.bind(
                user.getEmail(),
                user.getPassword(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                String.valueOf(user.getGender()),
                new Date()))
        // @formatter:on
    }

    @Override
    void delete(final User entity) {
        getSession().execute(new BoundStatement(deleteStatement).bind(entity.getEmail()))
    }

    @Override
    void deleteByKey(final String key) {
        getSession().execute(new BoundStatement(deleteStatement).bind(key))
    }

    @Override
    long count() {
        return getSession().execute('select count(*) from user').one().getLong(0)
    }

    static User toUser(Row row) {
        if (row == null || row.isNull('email')) {
            return null
        }
        final User user = new User(
                email: row.getString('email'),
                password: row.getString('password'),
                login: row.getString('login'),
                gender: Gender.valueOf(row.getString('gender')),
                firstName: row.getString('first_name'),
                lastName: row.getString('last_name')
        )
        return user
    }

    @Override
    void afterPropertiesSet() throws Exception {
        getByKeyStatement = session.prepare('select * from user where email = ?')
        addStatement = session.prepare(
                'insert into user (email, password, login, first_name, last_name, gender, create_date) ' +
                        'values (?, ?, ?, ?, ?, ?, ?)')
        deleteStatement = session.prepare('delete from user where email = ?')
    }
}
