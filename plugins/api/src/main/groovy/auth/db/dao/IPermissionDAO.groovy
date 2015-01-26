package auth.db.dao

import auth.api.v1.Group
import common.status.ISystemStatusComponent

/**
 * For Permissions manipulations
 */
interface IPermissionDAO extends ISystemStatusComponent {

    // Permissions
    Set<String> getGroupsPermissionsUnion(Set<String> groupCodes)

    Set<String> getGroupPermissions(final String code)

    void grantPermissionsToGroup(final String code, final Set<String> scopes)

    void revokeGroupPermissions(final String code, final Set<String> scopes)

    Set<String> getAccountPermissions(final String email)

    void grantPermissionsToAccount(final String email, final Set<String> scopes)

    void revokeAccountPermissions(final String email, final Set<String> scopes)

    void revokePermissions(final Set<String> scopes)

    void deleteGroupPermissions(final Group group)

    void deleteAccountPermissions(final String email)

    // Approvals

    Set<String> getAccountApprovals(final String ownerEmail, final String clientID)

    void approveAccountPermissions(final String ownerEmail, final String clientID, final Set<String> scopes)

    void revokeAccountApprovals(final String ownerEmail, final String clientID, final Set<String> scopes)

    void revokeApprovals(Set<String> scopes)

    void deleteOwnerApprovals(final String ownerEmail)

    void deleteClientApprovals(final String clientID)
}
