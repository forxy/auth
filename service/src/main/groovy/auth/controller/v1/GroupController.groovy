package auth.controller.v1

import auth.api.v1.pojo.Group
import auth.service.IGroupService
import common.pojo.SortDirection
import common.pojo.StatusEntity
import common.rest.AbstractService

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/groups/')
@Produces(MediaType.APPLICATION_JSON)
class GroupController extends AbstractService {

    IGroupService groupService

    @GET
    Response getGroups(@QueryParam('page') final Integer page,
                       @QueryParam('size') final Integer size,
                       @QueryParam('sort_dir') final SortDirection sortDirection,
                       @QueryParam('sorted_by') final String sortedBy,
                       @QueryParam('code') final String codeFilter,
                       @QueryParam('name') final String nameFilter,
                       @QueryParam('updated_by') final String updatedByFilter,
                       @QueryParam('created_by') final String createdByFilter,
                       @Context final UriInfo uriInfo,
                       @Context final HttpHeaders headers) {
        return respondWith(
                page == null && size == null ?
                        groupService.getAllGroups() :
                        groupService.getGroups(
                                page,
                                size,
                                sortDirection,
                                sortedBy,
                                new Group(
                                        code: codeFilter,
                                        name: nameFilter,
                                        updatedBy: updatedByFilter,
                                        createdBy: createdByFilter
                                )
                        ),
                uriInfo,
                headers
        ).build()
    }

    @GET
    @Path('/{code}/')
    Response getGroup(@PathParam('code') final String code,
                      @Context final UriInfo uriInfo,
                      @Context final HttpHeaders headers) {
        return respondWith(groupService.getGroup(code), uriInfo, headers).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response registerGroup(final Group group,
                           @Context final UriInfo uriInfo,
                           @Context final HttpHeaders headers) {
        groupService.createGroup(group)
        return Response.ok(new StatusEntity('200', "$uriInfo.absolutePath/$group.code")).build()
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateGroup(final Group group,
                         @Context final UriInfo uriInfo,
                         @Context final HttpHeaders headers) {
        groupService.updateGroup(group)
        return Response.ok(new StatusEntity('200', "$uriInfo.absolutePath/$group.code")).build()
    }

    @DELETE
    @Path('/{code}/')
    Response deleteGroup(@PathParam('code') final String code,
                         @Context final UriInfo uriInfo,
                         @Context final HttpHeaders headers) {
        groupService.deleteGroup(code)
        return Response.ok(new StatusEntity('200', "Group with code=$code has been successfully removed")).build()
    }
}
