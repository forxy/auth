package auth.service

import auth.api.v1.Account
import auth.api.v1.User
import common.exceptions.ServiceException

/**
 * Authentication manager
 */
interface IAuthenticationService {

    String login(final String login, final String password)

    User getProfile(final String email)

    void updateProfile(final User profile)

    Account authenticate(String authorizationHeader)

    Account authenticate(String clientID, String clientSecret)

    void authorize(final String clientID,
                   final Set<String> responseTypes,
                   final Account account,
                   final Set<String> scopes,
                   final String redirectUri) throws ServiceException

    String generateAccessCode()

    String generateAccessToken()

    String getIDToken(final Account account)
}
