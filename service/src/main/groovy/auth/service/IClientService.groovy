package auth.service

import auth.api.v1.Client
import common.api.EntityPage
import common.api.SortDirection

/**
 * Entry point into auth service business logic
 */
interface IClientService {

    Iterable<Client> getAllClients()

    EntityPage<Client> getClients(final Integer pageNumber, final Integer size, final SortDirection sortDirection,
                                  final String sortedBy, final Client filter)

    Client getClient(final String clientID)

    void updateClient(final Client auth)

    void createClient(final Client auth)

    void deleteClient(final String clientID)
}
