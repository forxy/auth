package auth.service

import auth.api.v1.Client
import auth.db.dao.IClientDAO
import auth.exceptions.AuthEvent
import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import org.joda.time.DateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Implementation class for ClientService business logic
 */
class ClientService implements IClientService {

    static final int DEFAULT_PAGE_SIZE = 10

    IClientDAO clientDAO

    PasswordEncoder passwordEncoder

    List<Client> getAllClients() {
        clientDAO.findAll().collect { it }
    }

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
            final Page<Client> page = clientDAO.findAll(pageRequest, filter)
            return new EntityPage<>(page.content, page.size, page.number, page.totalElements)
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, pageNumber)
        }
    }

    @Override
    Client getClient(final String clientID) {
        Client client = clientDAO.findOne(clientID)
        if (client == null) {
            throw new ServiceException(AuthEvent.ClientNotFound, clientID)
        }
        return client
    }

    @Override
    void updateClient(final Client client) {
        if (clientDAO.exists(client.clientID)) {
            client.updateDate = DateTime.now()
            clientDAO.save(client)
        } else {
            throw new ServiceException(AuthEvent.ClientNotFound, client.clientID)
        }
    }

    @Override
    void createClient(final Client client) {
        if (!client.clientID || !clientDAO.exists(client.clientID)) {
            if (!client.clientID) {
                client.clientID = UUID.randomUUID() as String
            }
            client.createDate = DateTime.now()
            client.updateDate = DateTime.now()
            client.password = passwordEncoder.encode(client.password)
            clientDAO.save(client)
        } else {
            throw new ServiceException(AuthEvent.ClientAlreadyExists, client.clientID)
        }
    }

    @Override
    void deleteClient(final String clientID) {
        if (clientDAO.exists(clientID)) {
            clientDAO.delete(clientID)
        } else {
            throw new ServiceException(AuthEvent.ClientNotFound, clientID)
        }
    }
}
