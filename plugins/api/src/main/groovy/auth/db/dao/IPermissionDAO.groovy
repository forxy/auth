package auth.db.dao

import common.status.ISystemStatusComponent

/**
 * For Permissions manipulations
 */
interface IPermissionDAO extends ISystemStatusComponent {

    // Permissions

    Set<String> getGroupsPermissionsUnion(final Set<String> codes, final String clientID)

    Set<String> getGroupPermissions(final String code, final String clientID)

    void grantPermissionsToGroup(final String code, final String clientID, final Set<String> scopes)

    void revokeGroupPermissions(final String code, final String clientID, final Set<String> scopes)

    Set<String> getAccountPermissions(final String email, final String clientID)

    void grantPermissionsToAccount(final String email, final String clientID, final Set<String> scopes)

    void revokeAccountPermissions(final String email, final String clientID, final Set<String> scopes)

    void deleteGroupPermissions(final String code)

    void deleteAccountPermissions(final String email)

    void deleteClientPermissions(final String clientID)

    // Approvals

    Set<String> getAccountApprovals(final String email, final String clientID)

    void approveAccountPermissions(final String email, final String clientID, final Set<String> scopes)

    void revokeAccountApprovals(final String email, final String clientID, final Set<String> scopes)

    void deleteAccountApprovals(final String email)

    void deleteClientApprovals(final String clientID)
}
