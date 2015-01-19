package auth.db.dao

/**
 * For Permissions manipulations
 */
interface IPermissionDAO {

    Set<String> getGroupsPermissionsUnion(final Set<String> codes, final String clientID)

    Set<String> getGroupPermissions(String code, String clientID)

    void grantPermissionsToGroup(String code, String clientID, Set<String> scopes)

    void revokeGroupPermissions(String code, String clientID, Set<String> scopes)

    Set<String> getAccountPermissions(String email, String clientID)

    void grantPermissionsToAccount(String email, String clientID, Set<String> scopes)

    void revokeAccountPermissions(String email, String clientID, Set<String> scopes)

    void deleteClientPermissions(String clientID)

    void deleteGroupPermissions(String code)

    void deleteAccountPermissions(String email)
}