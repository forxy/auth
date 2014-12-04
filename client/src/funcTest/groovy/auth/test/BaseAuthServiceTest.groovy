package auth.test

import org.junit.Before
import org.junit.Ignore
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

@Ignore
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
abstract class BaseAuthServiceTest extends AbstractJUnit4SpringContextTests {

    String transactionGUID = null

    @Before
    void setUp() {
        transactionGUID = UUID.randomUUID() as String;
    }
}
