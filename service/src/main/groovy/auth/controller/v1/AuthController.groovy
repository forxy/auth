package auth.controller.v1

import auth.api.v1.Account
import auth.api.v1.DiscoveryInfo
import auth.api.v1.Token
import auth.api.v1.User
import auth.service.IAuthenticationService
import common.api.StatusEntity
import common.rest.AbstractService

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.*

/**
 * Provides server-side authentication procedure
 */
@Path('/')
class AuthController extends AbstractService {

    IAuthenticationService authenticationService

    DiscoveryInfo discoveryInfo

    @POST
    @Path('/login')
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response login(@FormParam('login') String login,
                   @FormParam('password') String password,
                   @Context final UriInfo uriInfo,
                   @Context final HttpHeaders headers) {
        return respondWith(
                authenticationService.login(login, password),
                uriInfo,
                headers
        ).build()
    }

    /*@POST
    @Path('/register')
    Response register(final Credentials credentials,
                             @Context final UriInfo uriInfo,
                             @Context final HttpHeaders headers) {
        return respondWith(authenticationService.createUser(new User(credentials.getEmail(), credentials.getPassword())),
                uriInfo, headers).build()
    }*/

    @GET
    //@RolesAllowed('profile')
    @Path('/profile/{email}/')
    @Produces(MediaType.APPLICATION_JSON)
    Response profile(@PathParam('email') final String email,
                     @Context final UriInfo uriInfo,
                     @Context final HttpHeaders headers) {
        return respondWith(
                authenticationService.getProfile(email),
                uriInfo,
                headers
        ).build()
    }

    @PUT
    @RolesAllowed('profile')
    @Path('/profile/')
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateProfile(final User profile,
                           @Context final UriInfo uriInfo,
                           @Context final HttpHeaders headers) {
        authenticationService.updateProfile profile
        return Response.ok(new StatusEntity("$uriInfo.absolutePath/$profile.email")).build()
    }

    @POST
    @Path('/auth')
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response authorize(@FormParam('scope') String requestedScopes,
                       @FormParam('response_type') String responseTypesSet,
                       @FormParam('client_id') String clientID,
                       @FormParam('redirect_uri') String redirectUri,
                       @FormParam('state') String state,
                       @FormParam('nonce') String nonce,
                       @FormParam('include_granted_scopes') Boolean includeGrantedScopes,
                       @Context final UriInfo uriInfo,
                       @Context final HttpHeaders headers) {
        Account account = authenticationService.authenticate(headers.getHeaderString(HttpHeaders.AUTHORIZATION))
        if (account) {
            Set<String> respTypes = responseTypesSet?.split(' ') as Set ?: []
            Set<String> scopes = requestedScopes?.split(' ') as Set ?: []

            authenticationService.authorize(clientID, respTypes, account, scopes, redirectUri)

            redirectUri += "?state=$state"
            if (respTypes.contains('code')) redirectUri += "&code=${authenticationService.generateAccessCode()}"
            if (respTypes.contains('token')) redirectUri += "&token=${authenticationService.generateAccessToken()}"
            if (respTypes.contains('id_token')) redirectUri += "&id_token=${authenticationService.getIDToken(account)}"
            if (nonce) redirectUri += "&nonce=$nonce"
            return Response.temporaryRedirect(URI.create(redirectUri)).build()
        }

        return respondWith(Response.Status.UNAUTHORIZED, uriInfo, headers).build()
    }

    @POST
    @Path('/token')
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response getToken(@FormParam('code') String accessCode,
                      @FormParam('client_id') String clientID,
                      @FormParam('secret') String clientSecret,
                      @FormParam('redirect_uri') String redirectUri,
                      @FormParam('grant_type') String grantType,
                      @Context final UriInfo uriInfo,
                      @Context final HttpHeaders headers) {
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION)
        Account account = authorizationHeader ?
                authenticationService.authenticate(authorizationHeader) :
                authenticationService.authenticate(clientID, clientSecret)
        if (account && 'authorization_code' == grantType) {
            return respondWith(new Token(
                    accessToken: authenticationService.generateAccessToken(),
                    idToken: authenticationService.getIDToken(account),
                    refreshToken: authenticationService.generateAccessToken(),
                    expiresIn: 1800000,
                    tokenType: 'Bearer'
            ), uriInfo, headers).build()
        }
        return respondWith(Response.Status.UNAUTHORIZED, uriInfo, headers).build()
    }

    @GET
    @Path('/certs')
    Response jwks(@Context final UriInfo uriInfo,
                  @Context final HttpHeaders headers) {
        return respondWith(null, uriInfo, headers).build()
    }

    @GET
    @Path('/.well-known/openid-configuration')
    Response configuration(@Context final UriInfo uriInfo,
                           @Context final HttpHeaders headers) {
        return respondWith(discoveryInfo, uriInfo, headers).build()
    }
}
