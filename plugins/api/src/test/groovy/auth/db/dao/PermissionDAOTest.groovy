package auth.db.dao

import auth.api.v1.Group
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * Client Data Source tests
 */
abstract class PermissionDAOTest extends AbstractJUnit4SpringContextTests {

    static final String TEST_EMAIL = 'test@email.com'
    static final String TEST_GROUP_CODE = 'test'
    static final String TEST_RESOURCE_CLIENT_ID = UUID.randomUUID() as String
    static final String TEST_CLIENT_ID = UUID.randomUUID() as String

    @Autowired
    IPermissionDAO permissionDAO

    @Test
    void testGrantPermissionsToAccount() {
        permissionDAO.grantPermissionsToAccount(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID, ['test1'] as Set)
        assert permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Permissions not granted'
        permissionDAO.deleteClientPermissions(TEST_RESOURCE_CLIENT_ID)
        assert !permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
    }

    @Test
    void testGrantPermissionsToGroup() {
        permissionDAO.grantPermissionsToGroup(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID, ['test1'] as Set)
        assert permissionDAO.getGroupPermissions(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Permissions not granted'
        permissionDAO.deleteClientPermissions(TEST_RESOURCE_CLIENT_ID)
        assert !permissionDAO.getGroupPermissions(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
    }

    @Test
    void testRevokeAccountPermissions() {
        permissionDAO.grantPermissionsToAccount(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID, ['test1', 'test2'] as Set)
        assert permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID) == ['test1', 'test2'] as Set, 'Permissions not granted'
        permissionDAO.revokeAccountPermissions(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID, ['test2'] as Set)
        assert permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Permission "test2" should be revoked'
        permissionDAO.deleteAccountPermissions(TEST_EMAIL)
        assert !permissionDAO.getAccountPermissions(TEST_EMAIL, TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
    }

    @Test
    void testRevokeGroupPermissions() {
        permissionDAO.grantPermissionsToGroup(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID, ['test1', 'test2'] as Set)
        assert permissionDAO.getGroupPermissions(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID) == ['test1', 'test2'] as Set, 'Permissions not granted'
        permissionDAO.revokeGroupPermissions(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID, ['test2'] as Set)
        assert permissionDAO.getGroupPermissions(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Permission "test2" should be revoked'
        permissionDAO.deleteGroupPermissions(new Group(code: TEST_GROUP_CODE, members: []))
        assert !permissionDAO.getGroupPermissions(TEST_GROUP_CODE, TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
    }

    @Test
    void testGetGroupsPermissionsUnion() {
        permissionDAO.grantPermissionsToGroup('testGroup1', TEST_RESOURCE_CLIENT_ID, ['test1', 'test2'] as Set)
        permissionDAO.grantPermissionsToGroup('testGroup2', TEST_RESOURCE_CLIENT_ID, ['test2', 'test3'] as Set)
        permissionDAO.grantPermissionsToGroup('testGroup3', TEST_RESOURCE_CLIENT_ID, null)

        assert permissionDAO.getGroupsPermissionsUnion(
                ['testGroup1', 'testGroup2', 'testGroup3'] as Set,
                TEST_RESOURCE_CLIENT_ID
        ) == ['test1', 'test2', 'test3'] as Set, 'Invalid permissions union'

        permissionDAO.deleteClientPermissions(TEST_RESOURCE_CLIENT_ID)
        assert !permissionDAO.getGroupPermissions('testGroup1', TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
        assert !permissionDAO.getGroupPermissions('testGroup2', TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
        assert !permissionDAO.getGroupPermissions('testGroup3', TEST_RESOURCE_CLIENT_ID), 'Permissions have not been removed'
    }

    @Test
    void testApproveAccountPermissions() {
        permissionDAO.approveAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID, ['test1'] as Set)
        assert permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Permissions not approved'
        permissionDAO.deleteClientApprovals(TEST_CLIENT_ID)
        assert !permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID), 'Approvals have not been removed'
    }

    @Test
    void testRevokeAccountApprovals() {
        permissionDAO.approveAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID, ['test1', 'test2'] as Set)
        assert permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID) == ['test1', 'test2'] as Set, 'Permissions not approved'
        permissionDAO.revokeAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID, ['test2'] as Set)
        assert permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Approval "test2" should be revoked'
        permissionDAO.deleteOwnerApprovals(TEST_EMAIL)
        assert !permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID), 'Approvals have not been removed'
    }

    @Test
    void testDeleteResourceApprovals() {
        permissionDAO.approveAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID, ['test1'] as Set)
        assert permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID) == ['test1'] as Set, 'Permissions not approved'
        permissionDAO.deleteResourceApprovals(TEST_RESOURCE_CLIENT_ID)
        assert !permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, TEST_RESOURCE_CLIENT_ID), 'Approvals have not been removed'
    }

    @Test
    void testGetSystemStatus() {
        ComponentStatus status = permissionDAO.status
        assert status, 'DB status not available'
        assert status.componentType == ComponentStatus.ComponentType.DB
        assert status.status == StatusType.GREEN
        assert status.name == 'Permission DAO'
    }
}
