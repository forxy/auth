package auth.service

import auth.api.v1.pojo.Client
import auth.db.dao.IClientDAO
import auth.exceptions.AuthServiceEvent
import common.exceptions.ServiceException
import common.pojo.EntityPage
import common.pojo.SortDirection
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
    EntityPage<Client> getClients(final Integer page, final Integer size, final SortDirection sortDirection,
                                  final String sortedBy, final Client filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(page - 1, pageSize)
            }
            final Page<Client> p = clientDAO.findAll(pageRequest, filter)
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(AuthServiceEvent.InvalidPageNumber, page)
        }
    }

    @Override
    Client getClient(final String clientID) {
        Client client = clientDAO.findOne(clientID)
        if (client == null) {
            throw new ServiceException(AuthServiceEvent.ClientNotFound, clientID)
        }
        return client
    }

    @Override
    void updateClient(final Client client) {
        if (clientDAO.exists(client.getClientID())) {
            clientDAO.save(client)
        } else {
            throw new ServiceException(AuthServiceEvent.ClientNotFound, client.getClientID())
        }
    }

    @Override
    void createClient(final Client client) {
        if (client.getClientID() == null) {
            client.setClientID(UUID.randomUUID().toString())
        }
        client.setCreateDate(new Date())
        client.setSecret(passwordEncoder.encode(client.getSecret()))
        if (!clientDAO.exists(client.getClientID())) {
            clientDAO.save(client)
        } else {
            throw new ServiceException(AuthServiceEvent.ClientAlreadyExists, client.getClientID())
        }
    }

    @Override
    void deleteClient(final String clientID) {
        if (clientDAO.exists(clientID)) {
            clientDAO.delete(clientID)
        } else {
            throw new ServiceException(AuthServiceEvent.ClientNotFound, clientID)
        }
    }
}
