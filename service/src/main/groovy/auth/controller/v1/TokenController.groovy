package auth.controller.v1

import auth.api.v1.Token
import auth.service.ITokenService
import common.api.SortDirection
import common.api.StatusEntity
import common.rest.AbstractService

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/tokens/')
@Produces(MediaType.APPLICATION_JSON)
class TokenController extends AbstractService {

    ITokenService tokenService

    @GET
    Response getTokens(@QueryParam('page') final Integer page,
                       @QueryParam('size') final Integer size,
                       @QueryParam('sort_dir') final SortDirection sortDirection,
                       @QueryParam('sorted_by') final String sortedBy,
                       @QueryParam('') final Token filter,
                       @Context final UriInfo uriInfo,
                       @Context final HttpHeaders headers) {
        return respondWith(
                page == null && size == null ?
                        tokenService.getAllTokens() :
                        tokenService.getTokens(page, size, sortDirection, sortedBy, filter),
                uriInfo,
                headers
        ).build()
    }

    @GET
    @Path('/{token_key}/')
    Response getToken(@PathParam('token_key') final String tokenKey,
                      @Context final UriInfo uriInfo,
                      @Context final HttpHeaders headers) {
        return respondWith(tokenService.getToken(tokenKey), uriInfo, headers).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response registerToken(final Token token,
                           @Context final UriInfo uriInfo,
                           @Context final HttpHeaders headers) {
        tokenService.createToken(token)
        return Response.ok(new StatusEntity("$uriInfo.absolutePath/$token.tokenKey")).build()
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateToken(final Token token,
                         @Context final UriInfo uriInfo,
                         @Context final HttpHeaders headers) {
        tokenService.updateToken(token)
        return Response.ok(new StatusEntity("$uriInfo.absolutePath/$token.tokenKey")).build()
    }

    @DELETE
    @Path('/{token_key}/')
    Response deleteToken(@PathParam('token_key') final String tokenKey,
                         @Context final UriInfo uriInfo,
                         @Context final HttpHeaders headers) {
        tokenService.deleteToken(tokenKey)
        return Response.ok(new StatusEntity("Token with token_key=$tokenKey has been successfully removed")).build()
    }

    void setTokenService(ITokenService tokenService) {
        this.tokenService = tokenService
    }
}