package auth.controller.v1

import auth.api.v1.AuthorizationType
import auth.api.v1.DiscoveryInfo
import auth.api.v1.User
import auth.security.IJWTManager
import auth.service.IAuthenticationService
import com.nimbusds.jose.JWSObject
import common.rest.AbstractService
import net.minidev.json.JSONObject

import javax.ws.rs.*
import javax.ws.rs.core.*

/**
 * Provides server-side authentication procedure
 */
@Path('/')
class AuthController extends AbstractService {

    IAuthenticationService authenticationService

    DiscoveryInfo discoveryInfo

    IJWTManager jwtManager

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

    @POST
    @Path('/auth')
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response authorize(@FormParam('client_id') String clientID,
                   @FormParam('response_type') String responseTypesSet,
                   @FormParam('scope') String requestedScopes,
                   @FormParam('redirect_uri') String redirectUri,
                   @FormParam('state') String state,
                   @Context final UriInfo uriInfo,
                   @Context final HttpHeaders headers) {
        String[] authorization = headers.getHeaderString(HttpHeaders.AUTHORIZATION)?.split(' ')
        if (authorization && authorization[0] == 'Bearer') {
            JWSObject jwt = jwtManager.fromJWT(authorization[1])
            JSONObject jwtBody = jwt.payload.toJSONObject()
            String authorizationCode = authenticationService.authorize(
                    clientID,
                    responseTypesSet.split(' ').collect{ AuthorizationType.valueOf(it)} as Set,
                    jwtBody.get('sub') as String,
                    responseTypesSet.split(' ') as Set,
                    redirectUri
            )
            redirectUri += "?state=$state&code=$authorizationCode"
            return Response.temporaryRedirect(URI.create(redirectUri)).build()
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
