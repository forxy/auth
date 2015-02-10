package auth.db.dao

import common.status.ISystemStatusComponent

/**
 * Data Access Object for tokens and authorization codes management.
 */
interface ITokenDAO extends ISystemStatusComponent {

    void grantAccessCode(final String code, final String clientID, final String email)

    String getAccessCode(final String clientID, final String email)

    List<String> getAccessCodesByClient(final String clientID)

    List<String> getAccessCodesByAccount(final String email)

    void deleteAccessCodesByClient(final String clientID)

    void deleteAccessCodesByAccount(final String email)

    void grantToken(final String token, final String code)

    void grantToken(final String token, final String refreshToken, final String code)

    void revokeTokensByAccessCode(final String code)
}

