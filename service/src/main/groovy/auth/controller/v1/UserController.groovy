package auth.controller.v1

import auth.api.v1.pojo.Gender
import auth.api.v1.pojo.User
import auth.service.IUserService
import common.pojo.SortDirection
import common.pojo.StatusEntity
import common.rest.AbstractService

import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/users/')
@Produces(MediaType.APPLICATION_JSON)
class UserController extends AbstractService {

    IUserService userService

    @GET
    @RolesAllowed('users_get')
    Response getUsers(@QueryParam('page') final Integer page,
                      @QueryParam('size') final Integer size,
                      @QueryParam('sort_dir') final SortDirection sortDirection,
                      @QueryParam('sorted_by') final String sortedBy,
                      @QueryParam('email') final String emailFilter,
                      @QueryParam('login') final String loginFilter,
                      @QueryParam('first_name') final String firstNameFilter,
                      @QueryParam('last_name') final String lastNameFilter,
                      @QueryParam('gender') final Gender genderFilter,
                      @Context final UriInfo uriInfo,
                      @Context final HttpHeaders headers) {
        return respondWith(
                page == null && size == null ?
                        userService.getAllUsers() :
                        userService.getUsers(
                                page,
                                size,
                                sortDirection,
                                sortedBy,
                                new User(email: emailFilter,
                                        login: loginFilter,
                                        firstName: firstNameFilter,
                                        lastName: lastNameFilter,
                                        gender: genderFilter
                                )
                        ),
                uriInfo,
                headers
        ).build()
    }

    @GET
    @RolesAllowed('users_get')
    @Path('/{email}/')
    Response getUser(@PathParam('email') final String email,
                     @Context final UriInfo uriInfo,
                     @Context final HttpHeaders headers) {
        return respondWith(userService.getUser(email), uriInfo, headers).build()
    }

    @POST
    @RolesAllowed('users_manage')
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(final User user,
                        @Context final UriInfo uriInfo,
                        @Context final HttpHeaders headers) {
        userService.createUser(user)
        return Response.ok(new StatusEntity('200', "$uriInfo.absolutePath/$user.email")).build()
    }

    @PUT
    @RolesAllowed('users_manage')
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateUser(final User user,
                        @Context final UriInfo uriInfo,
                        @Context final HttpHeaders headers) {
        userService.updateUser(user)
        return Response.ok(new StatusEntity('200', "$uriInfo.absolutePath/$user.email")).build()
    }

    @DELETE
    @RolesAllowed('users_manage')
    @Path('/{email}/')
    Response deleteUser(@PathParam('email') final String email,
                        @Context final UriInfo uriInfo,
                        @Context final HttpHeaders headers) {
        userService.deleteUser(email)
        return Response.ok(new StatusEntity('200', "User with email=$email has been successfully removed")).build()
    }
}
