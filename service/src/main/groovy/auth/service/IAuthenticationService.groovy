package auth.service

import auth.api.v1.User

/**
 * Authentication manager
 */
interface IAuthenticationService {

    String login(String login, String password)

    User getProfile(final String email)
}
