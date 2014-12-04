package auth.controller.v1

import auth.api.v1.Client
import auth.service.IClientService
import common.pojo.SortDirection
import common.pojo.StatusEntity
import common.rest.AbstractService

import javax.ws.rs.*
import javax.ws.rs.core.*

@Path('/clients/')
@Produces(MediaType.APPLICATION_JSON)
class ClientController extends AbstractService {

    IClientService clientService

    @GET
    Response getClients(@QueryParam('page') final Integer page,
                        @QueryParam('size') final Integer size,
                        @QueryParam('sort_dir') final SortDirection sortDirection,
                        @QueryParam('sorted_by') final String sortedBy,
                        @QueryParam('client_id') final String idFilter,
                        @QueryParam('name') final String nameFilter,
                        @QueryParam('updated_by') final String updatedByFilter,
                        @QueryParam('created_by') final String createdByFilter,
                        @Context final UriInfo uriInfo,
                        @Context final HttpHeaders headers) {
        return respondWith(
                page == null && size == null ?
                        clientService.getAllClients() :
                        clientService.getClients(
                                page,
                                size,
                                sortDirection,
                                sortedBy,
                                new Client(
                                        clientID: idFilter,
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
    @Path('/{client_id}/')
    Response getClient(@PathParam('client_id') final String clientID,
                       @Context final UriInfo uriInfo,
                       @Context final HttpHeaders headers) {
        return respondWith(clientService.getClient(clientID), uriInfo, headers).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response registerClient(final Client client,
                            @Context final UriInfo uriInfo,
                            @Context final HttpHeaders headers) {
        clientService.createClient(client)
        return Response.ok(new StatusEntity('200', "$uriInfo.absolutePath/$client.clientID")).build()
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateClient(final Client client,
                          @Context final UriInfo uriInfo,
                          @Context final HttpHeaders headers) {
        clientService.updateClient(client)
        return Response.ok(new StatusEntity('200', "$uriInfo.absolutePath/$client.clientID")).build()
    }

    @DELETE
    @Path('/{client_id}/')
    Response deleteClient(@PathParam('client_id') final String clientID,
                          @Context final UriInfo uriInfo,
                          @Context final HttpHeaders headers) {
        clientService.deleteClient(clientID)
        return Response.ok(new StatusEntity('200', "Client with ID=$clientID has been successfully removed")).build()
    }
}
