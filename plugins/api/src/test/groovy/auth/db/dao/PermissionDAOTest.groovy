package auth.db.dao

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * Client Data Source tests
 */
abstract class PermissionDAOTest extends AbstractJUnit4SpringContextTests {

    static final String TEST_EMAIL = 'test@email.com'
    static final String TEST_GROUP_CODE = 'test'
    static final String TEST_CLIENT_ID = UUID.randomUUID() as String

    @Autowired
    IPermissionDAO permissionDAO

    @Test
    void testGrantPermissions() {
        permissionDAO.grantPermissionsToAccount(TEST_EMAIL, TEST_CLIENT_ID, ['test1'] as Set)
        assert permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID) == ['test1'] as Set, 'Permissions not granted'
        permissionDAO.deleteClientPermissions(TEST_CLIENT_ID)
        assert !permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID), 'Permissions have not been removed'
    }
}
