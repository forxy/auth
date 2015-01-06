package auth.service

import auth.api.v1.Profile

/**
 * Authentication manager
 */
interface IAuthenticationService {

    String login(String login, String password)

    Profile getProfile(final String email)
}
