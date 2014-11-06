package auth.db.dao.cassandra.client;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.InitializingBean;
import auth.api.v1.pojo.Gender;
import auth.api.v1.pojo.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Cassandra client implementation for User entity
 */
public class CassandraUserClient implements ICassandraClient<User>, InitializingBean {

    private PreparedStatement getByKeyStatement;
    private PreparedStatement getPageStatement;
    private PreparedStatement addStatement;
    private PreparedStatement deleteStatement;

    private Session session;

    @Override
    public List<User> getAll() {
        List<Row> rows = getSession().execute("select * from user;").all();
        List<User> users = new ArrayList<User>(rows.size());
        for (final Row row : rows) {
            users.add(toUser(row));
        }
        return users;
    }

    @Override
    public List<User> getPage(final long start, final long size) {
        return null;
    }

    @Override
    public User getByKey(final String key) {
        return toUser(getSession().execute(new BoundStatement(getByKeyStatement).bind(key)).one());
    }

    @Override
    public boolean existsKey(final String key) {
        return getSession().execute(new BoundStatement(getByKeyStatement).bind(key)).one() != null;
    }

    @Override
    public boolean exists(final User entity) {
        return getSession().execute(new BoundStatement(getByKeyStatement).bind(entity.getEmail())).one() != null;
    }

    @Override
    public void add(final User user) {
        final BoundStatement newUserStatement = new BoundStatement(addStatement);
        /*newUserStatement.setString("email", user.getEmail());
        newUserStatement.setBytes("password", ByteBuffer.wrap(user.getPassword()));
        newUserStatement.setString("login", user.getLogin());
        newUserStatement.setString("first_name", user.getFirstName());
        newUserStatement.setString("last_name", user.getLastName());
        newUserStatement.setString("gender", String.valueOf(user.getGender()));
        newUserStatement.setDate("birth_date", user.getBirthDate());*/
        // @formatter:off
        getSession().execute(newUserStatement.bind(
                user.getEmail(),
                user.getPassword(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                String.valueOf(user.getGender()),
                new Date()));
        // @formatter:on
    }

    @Override
    public void delete(final User entity) {
        getSession().execute(new BoundStatement(deleteStatement).bind(entity.getEmail()));
    }

    @Override
    public void deleteByKey(final String key) {
        getSession().execute(new BoundStatement(deleteStatement).bind(key));
    }

    @Override
    public long count() {
        return getSession().execute("select count(*) from user;").one().getLong(0);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    private User toUser(Row row) {
        if (row == null || row.isNull("email")) {
            return null;
        }
        String password = row.getString("password");
        final User user = new User(row.getString("email"), password);
        user.setLogin(row.getString("login"));
        user.setGender(Gender.valueOf(row.getString("gender")));
        user.setFirstName(row.getString("first_name"));
        user.setLastName(row.getString("last_name"));
        return user;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getByKeyStatement = session.prepare("select * from user where email = ?;");
        addStatement = session.prepare(
                "insert into user (email, password, login, first_name, last_name, gender, create_date) " +
                        "values (?, ?, ?, ?, ?, ?, ?);");
        deleteStatement = session.prepare("delete from user where email = ?;");
    }
}
