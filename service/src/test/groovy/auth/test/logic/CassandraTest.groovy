package auth.test.logic

import auth.test.BaseUserServiceTest
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Unit test to check cassandra
 */
class CassandraTest extends BaseUserServiceTest {

    static final Logger LOGGER = LoggerFactory.getLogger(CassandraTest.class)

    Cluster cluster = Cluster.builder().addContactPoint('localhost').build()

    @Ignore
    @Test
    void testCassandraJavaDriver() throws IOException {
        Session session = cluster.connect('auth')
        List<Row> rows = session.execute('select * from user').all()
        Assert.assertNotNull(rows)
        session.close()
    }
}
