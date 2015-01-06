package auth.service

import auth.api.v1.Profile
import auth.api.v1.User
import auth.db.dao.IProfileDAO
import auth.db.dao.IUserDAO
import auth.exceptions.AuthEvent
import auth.security.IJWTManager
import com.nimbusds.jose.JOSEException
import common.exceptions.ServiceException

/**
 * Authentication Manager implementation
 */
class AuthenticationService implements IAuthenticationService {

    IProfileDAO profileDAO

    IUserDAO userDAO

    IJWTManager jwtManager

    @Override
    String login(String login, String password) {
        User user = userDAO.authenticate(login, password)
        if (user) {
            try {
                return jwtManager.toJWT(user)
            } catch (JOSEException e) {
                throw new ServiceException(e, AuthEvent.InvalidCredentials, login)
            }
        }
        throw new ServiceException(AuthEvent.InvalidCredentials, login)
    }

    @Override
    Profile getProfile(final String email) {
        Profile user = profileDAO.getProfile(email)
        if (user == null) {
            throw new ServiceException(AuthEvent.UserNotFound, email)
        }
        return user
    }
}
