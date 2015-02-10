package auth.service

import auth.api.v1.Account
import auth.api.v1.Client
import auth.api.v1.User
import auth.db.dao.IClientDAO
import auth.db.dao.IPermissionDAO
import auth.db.dao.ITokenDAO
import auth.db.dao.IUserDAO
import auth.db.exceptions.AuthDBEvent
import auth.exceptions.AuthEvent
import auth.security.IJWTManager
import auth.util.RandomValueStringGenerator
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSObject
import common.exceptions.ServiceException
import net.minidev.json.JSONObject
import org.apache.commons.codec.binary.Base64

/**
 * Authentication implementation
 */
class AuthenticationService implements IAuthenticationService {

    IUserDAO userDAO
    IClientDAO clientDAO
    IPermissionDAO permissionDAO
    ITokenDAO tokenDAO

    IJWTManager jwtManager
    RandomValueStringGenerator randomGenerator

    @Override
    String login(final String login, final String password) {
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
    Account authenticate(final String authorizationHeader) {
        String[] authorization = authorizationHeader?.split(' ')
        if (authorization) {
            switch (authorization[0]) {
                case 'Bearer':
                    String id_token = authorization[1]
                    JWSObject jwt = jwtManager.fromJWT(id_token)
                    JSONObject jwtBody = jwt.payload.toJSONObject()
                    String email = jwtBody.get('sub') as String
                    return userDAO.find(email) ?: clientDAO.find(email)
                case 'Basic':
                    String[] loginAndPassword = (Base64.decodeBase64(authorization[1]) as String).split(':')
                    return userDAO.authenticate(loginAndPassword[0], loginAndPassword[1]) ?:
                            clientDAO.authenticate(loginAndPassword[0], loginAndPassword[1])
                default: throw new ServiceException(AuthEvent.NotEnoughPermissions)
            }
        }
    }

    @Override
    Account authenticate(final String clientID, final String clientSecret) {
        return clientDAO.authenticate(clientID, clientSecret)
    }

    @Override
    void authorize(final String clientID,
                   final Set<String> responseTypes,
                   final Account account,
                   final Set<String> scopes,
                   final String redirectUri) {
        Set<String> permissions =
                permissionDAO.getGroupsPermissionsUnion(account.groups) +
                        permissionDAO.getAccountPermissions(account.email)
        Client client = clientDAO.find clientID
        if (!client) {
            throw new ServiceException(AuthEvent.NotEnoughPermissions)
        }
        if (!client?.redirectUris?.contains(redirectUri)) {
            throw new ServiceException(AuthEvent.NotEnoughPermissions)
        }
        if (responseTypes.contains('id_token') && !scopes.contains('openid')) {
            throw new ServiceException(AuthEvent.NotEnoughPermissions)
        }
        if (!permissions.containsAll(scopes)) {
            throw new ServiceException(AuthEvent.NotEnoughPermissions)
        }
    }

    @Override
    String generateAccessCode() { randomGenerator.generate() }

    @Override
    String generateAccessToken() {
        return randomGenerator.generate()
    }

    @Override
    String getIDToken(final Account account) {
        return jwtManager.toJWT(account)
    }
}
