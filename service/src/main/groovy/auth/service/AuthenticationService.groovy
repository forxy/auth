package auth.service

import auth.api.v1.AuthorizationType
import auth.api.v1.User
import auth.db.dao.IPermissionDAO
import auth.db.dao.IUserDAO
import auth.db.exceptions.AuthDBEvent
import auth.exceptions.AuthEvent
import auth.security.IJWTManager
import auth.util.RandomValueStringGenerator
import com.nimbusds.jose.JOSEException
import common.exceptions.ServiceException

/**
 * Authentication implementation
 */
class AuthenticationService implements IAuthenticationService {

    IUserDAO userDAO
    IPermissionDAO permissionDAO

    IJWTManager jwtManager
    RandomValueStringGenerator randomGenerator

    @Override
    String login(String login, String password) {
        User user = userDAO.authenticate login, password
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
    User getProfile(final String email) {
        User user = userDAO.getProfile(email)
        if (!user) {
            throw new ServiceException(AuthDBEvent.UserNotFound, email)
        }
        return user
    }

    @Override
    void updateProfile(User profile) { userDAO.updateProfile profile }

    @Override
    String authorize(final String clientID,
                     final Set<AuthorizationType> responseType,
                     final String email,
                     final Set<String> scopes,
                     final String redirectUri) {
        User user = userDAO.find email
        Set<String> permissions =
                permissionDAO.getGroupsPermissionsUnion(user.groups) + permissionDAO.getAccountPermissions(user.email)
        if (permissions.containsAll(scopes)) {
            return randomGenerator.generate()
        } else {
            throw new ServiceException(AuthEvent.NotEnoughPermissions)
        }
    }
}
