package auth.service

import auth.api.v1.Credentials
import auth.api.v1.Profile
import auth.api.v1.User
import auth.db.dao.IProfileDAO
import auth.db.dao.IUserDAO
import auth.exceptions.AuthServiceEvent
import auth.security.IJWTManager
import com.nimbusds.jose.JOSEException
import common.exceptions.ServiceException
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Authentication Manager implementation
 */
class AuthenticationService implements IAuthenticationService {

    PasswordEncoder passwordEncoder

    IProfileDAO profileDAO

    IUserDAO userDAO

    IJWTManager jwtManager

    @Override
    String login(Credentials credentials) {
        User user = userDAO.findOne(credentials.getEmail())
        if (user != null) {
            if (passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
                try {
                    return jwtManager.toJWT(user)
                } catch (JOSEException e) {
                    throw new ServiceException(e, AuthServiceEvent.NotAuthorized, credentials.getEmail())
                }
            }
        }
        throw new ServiceException(AuthServiceEvent.NotAuthorized, credentials.getEmail())
    }

    @Override
    Profile getProfile(final String email) {
        Profile user = profileDAO.findOne(email)
        if (user == null) {
            throw new ServiceException(AuthServiceEvent.UserNotFound, email)
        }
        return user
    }
}