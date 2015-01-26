package auth.service

import auth.api.v1.AuthorizationType
import auth.api.v1.User

/**
 * Authentication manager
 */
interface IAuthenticationService {

    String login(final String login, final String password)

    User getProfile(final String email)

    void updateProfile(final User profile)

    String authorize(final String clientID,
                     final Set<AuthorizationType> responseType,
                     final String email,
                     final Set<String> scopes,
                     final String redirectUri)
}
