package auth.db.dao

import auth.api.v1.Group
import common.status.api.ComponentStatus
import common.status.api.StatusType
import spock.lang.Specification

import javax.annotation.Resource

/**
 * Permission Data Source tests
 */
abstract class PermissionDAOTest extends Specification {

    static final String TEST_EMAIL = 'test@email.com'
    static final String TEST_GROUP_CODE = 'test'
    static final String TEST_CLIENT_ID = UUID.randomUUID() as String

    @Resource
    IPermissionDAO permissionDAO

    def "permissions should be granted to Account and then revoked"() {
        when:
        permissionDAO.grantPermissionsToAccount TEST_EMAIL, ['test1'] as Set
        then:
        permissionDAO.getAccountPermissions(TEST_EMAIL) == ['test1'] as Set

        when:
        permissionDAO.revokePermissions(['test1'] as Set)
        then:
        !permissionDAO.getAccountPermissions(TEST_EMAIL)
    }

    def "permissions should be granted to Group and then revoked"() {
        when:
        permissionDAO.grantPermissionsToGroup(TEST_GROUP_CODE, ['test1'] as Set)
        then:
        permissionDAO.getGroupPermissions(TEST_GROUP_CODE) == ['test1'] as Set

        when:
        permissionDAO.revokePermissions(['test1'] as Set)
        then:
        !permissionDAO.getGroupPermissions(TEST_GROUP_CODE)
    }

    def "one of account permissions should be revoked"() {
        when:
        permissionDAO.grantPermissionsToAccount(TEST_EMAIL, ['test1', 'test2'] as Set)
        then:
        permissionDAO.getAccountPermissions(TEST_EMAIL) == ['test1', 'test2'] as Set

        when:
        permissionDAO.revokeAccountPermissions(TEST_EMAIL, ['test2'] as Set)
        then:
        permissionDAO.getAccountPermissions(TEST_EMAIL) == ['test1'] as Set

        when:
        permissionDAO.deleteAccountPermissions(TEST_EMAIL)
        then:
        !permissionDAO.getAccountPermissions(TEST_EMAIL)
    }


    def "one of group permissions should be revoked"() {
        when:
        permissionDAO.grantPermissionsToGroup(TEST_GROUP_CODE, ['test1', 'test2'] as Set)
        then:
        permissionDAO.getGroupPermissions(TEST_GROUP_CODE) == ['test1', 'test2'] as Set

        when:
        permissionDAO.revokeGroupPermissions(TEST_GROUP_CODE, ['test2'] as Set)
        then:
        permissionDAO.getGroupPermissions(TEST_GROUP_CODE) == ['test1'] as Set

        when:
        permissionDAO.deleteGroupPermissions(new Group(code: TEST_GROUP_CODE, members: []))
        then:
        !permissionDAO.getGroupPermissions(TEST_GROUP_CODE)
    }

    def "permissions DAO should return a union of permissions for a set of groups"() {
        setup:
        permissionDAO.grantPermissionsToGroup('testGroup1', ['test1', 'test2'] as Set)
        permissionDAO.grantPermissionsToGroup('testGroup2', ['test2', 'test3'] as Set)
        permissionDAO.grantPermissionsToGroup('testGroup3', null)

        expect:
        permissionDAO.getGroupsPermissionsUnion(
                ['testGroup1', 'testGroup2', 'testGroup3'] as Set) ==
                ['test1', 'test2', 'test3'] as Set

        cleanup:
        permissionDAO.revokePermissions(['test1', 'test2', 'test3'] as Set)
    }

    def "permissions should be approved to Account and then deleted"() {
        when:
        permissionDAO.approveAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID, ['test1'] as Set)
        then:
        permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID) == ['test1'] as Set

        when:
        permissionDAO.deleteClientApprovals(TEST_CLIENT_ID)
        then:
        !permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID)
    }

    def "one of account approvals should be removed"() {
        when:
        permissionDAO.approveAccountPermissions(TEST_EMAIL, TEST_CLIENT_ID, ['test1', 'test2'] as Set)
        then:
        permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID) == ['test1', 'test2'] as Set

        when:
        permissionDAO.revokeAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID, ['test2'] as Set)
        then:
        permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID) == ['test1'] as Set

        when:
        permissionDAO.deleteOwnerApprovals(TEST_EMAIL)
        then:
        !permissionDAO.getAccountApprovals(TEST_EMAIL, TEST_CLIENT_ID)
    }

    def "permissions DAO should return GREEN system status"() {
        given:
        ComponentStatus status = permissionDAO.status

        expect:
        status
        status.componentType == ComponentStatus.ComponentType.DB
        status.status == StatusType.GREEN
        status.name == 'Permission DAO'
    }
}
