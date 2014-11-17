package auth.service

import auth.api.v1.pojo.Credentials
import auth.api.v1.pojo.Profile
import auth.api.v1.pojo.User
import auth.db.dao.IProfileDAO
import auth.db.dao.IUserDAO
import auth.exceptions.AuthServiceEventLogID
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
                    throw new ServiceException(e, AuthServiceEventLogID.NotAuthorized, credentials.getEmail())
                }
            }
        }
        throw new ServiceException(AuthServiceEventLogID.NotAuthorized, credentials.getEmail())
    }

    @Override
    Profile getProfile(final String email) {
        Profile user = profileDAO.findOne(email)
        if (user == null) {
            throw new ServiceException(AuthServiceEventLogID.UserNotFound, email)
        }
        return user
    }
}
