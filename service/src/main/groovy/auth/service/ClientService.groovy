package auth.service

import auth.api.v1.Client
import auth.db.dao.IClientDAO
import auth.db.exceptions.AuthDBEvent
import auth.exceptions.AuthEvent
import common.api.EntityPage
import common.api.SortDirection
import common.exceptions.ServiceException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Implementation class for clients/applications management
 */
class ClientService implements IClientService {

    static final int DEFAULT_PAGE_SIZE = 10

    IClientDAO clientDAO

    PasswordEncoder passwordEncoder

    List<Client> getAllClients() { clientDAO.findAll() }

    @Override
    EntityPage<Client> getClients(final Integer pageNumber, final Integer size, final SortDirection sortDirection,
                                  final String sortedBy, final Client filter) {
        if (pageNumber >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(pageNumber - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(pageNumber - 1, pageSize)
            }
            final Page<Client> page = clientDAO.find pageRequest, filter
            return new EntityPage<>(page.content, page.size, page.number, page.totalElements)
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, pageNumber)
        }
    }

    @Override
    Client getClient(final String clientID) {
        Client client = clientDAO.find clientID
        if (client == null) {
            throw new ServiceException(AuthDBEvent.ClientNotFound, clientID)
        }
        return client
    }

    @Override
    void updateClient(final Client client) { clientDAO.save client }

    @Override
    void createClient(final Client client) { clientDAO.create client }

    @Override
    void deleteClient(final String clientID) { clientDAO.delete clientID }
}
