package auth.service

import auth.api.v1.Credentials
import auth.api.v1.Profile

/**
 * Authentication manager
 */
interface IAuthenticationService {

    String login(Credentials credentials)

    Profile getProfile(final String email)
}
