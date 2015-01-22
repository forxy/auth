package auth.db.dao

import auth.api.v1.Group
import common.status.ISystemStatusComponent

/**
 * For Permissions manipulations
 */
interface IPermissionDAO extends ISystemStatusComponent {

    // Permissions

    Set<String> getGroupsPermissionsUnion(final Set<String> codes, final String resourceClientID)

    Set<String> getGroupPermissions(final String code, final String resourceClientID)

    void grantPermissionsToGroup(final String code, final String resourceClientID, final Set<String> scopes)

    void revokeGroupPermissions(final String code, final String resourceClientID, final Set<String> scopes)

    Set<String> getAccountPermissions(final String email, final String resourceClientID)

    void grantPermissionsToAccount(final String email, final String resourceClientID, final Set<String> scopes)

    void revokeAccountPermissions(final String email, final String resourceClientID, final Set<String> scopes)

    void revokeResourcePermissions(final String resourceClientID, final Set<String> scopes)

    void deleteGroupPermissions(final Group group)

    void deleteAccountPermissions(final String email)

    void deleteClientPermissions(final String resourceClientID)

    // Approvals

    Set<String> getAccountApprovals(final String ownerEmail, final String clientID, final String resourceClientID)

    void approveAccountPermissions(final String ownerEmail, final String clientID, final String resourceClientID, final Set<String> scopes)

    void revokeAccountApprovals(final String ownerEmail, final String clientID, final String resourceClientID, final Set<String> scopes)

    void deleteOwnerApprovals(final String email)

    void deleteClientApprovals(final String clientID)

    void deleteResourceApprovals(final String resourceClientID)
}
