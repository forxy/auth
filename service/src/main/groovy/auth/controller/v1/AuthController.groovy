package auth.controller.v1

import auth.api.v1.Credentials
import auth.api.v1.DiscoveryInfo
import auth.service.IAuthenticationService
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
    Response login(@FormParam('email') String email,
                   @FormParam('password') String password,
                   @Context final UriInfo uriInfo,
                   @Context final HttpHeaders headers) {
        return respondWith(
                authenticationService.login(
                        new Credentials(
                                email: email,
                                password: password
                        )
                ),
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
    @RolesAllowed('profile')
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

    @GET
    @Path('/certs')
    Response jwks(@Context final UriInfo uriInfo,
                  @Context final HttpHeaders headers) {
        return respondWith(null, uriInfo, headers).build()
    }

    @GET
    @Path('/.well-known/openid-configuration')
    Response configuration(final Credentials credentials,
                           @Context final UriInfo uriInfo,
                           @Context final HttpHeaders headers) {
        return respondWith(discoveryInfo, uriInfo, headers).build()
    }
}
