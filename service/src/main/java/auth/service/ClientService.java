package auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import auth.db.dao.IClientDAO;
import auth.exceptions.AuthServiceEventLogId;
import auth.api.v1.pojo.Client;
import common.exceptions.ServiceException;
import common.pojo.EntityPage;
import common.pojo.SortDirection;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation class for ClientService business logic
 */
public class ClientService implements IClientService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private IClientDAO clientDAO;

    private PasswordEncoder passwordEncoder;

    public List<Client> getAllClients() {
        List<Client> allClients = new LinkedList<>();
        for (Client client : clientDAO.findAll()) {
            allClients.add(client);
        }
        return allClients;
    }

    @Override
    public EntityPage<Client> getClients(final Integer page, final Integer size, final SortDirection sortDirection,
                                         final String sortedBy, final Client filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size;
            PageRequest pageRequest;
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy);
            } else {
                pageRequest = new PageRequest(page - 1, pageSize);
            }
            final Page<Client> p = clientDAO.findAll(pageRequest, filter);
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements());
        } else {
            throw new ServiceException(AuthServiceEventLogId.InvalidPageNumber, page);
        }
    }

    @Override
    public Client getClient(final String clientID) {
        Client client = clientDAO.findOne(clientID);
        if (client == null) {
            throw new ServiceException(AuthServiceEventLogId.ClientNotFound, clientID);
        }
        return client;
    }

    @Override
    public void updateClient(final Client client) {
        if (clientDAO.exists(client.getClientID())) {
            clientDAO.save(client);
        } else {
            throw new ServiceException(AuthServiceEventLogId.ClientNotFound, client.getClientID());
        }
    }

    @Override
    public void createClient(final Client client) {
        if (client.getClientID() == null) {
            client.setClientID(UUID.randomUUID().toString());
        }
        client.setCreateDate(new Date());
        client.setSecret(passwordEncoder.encode(client.getSecret()));
        if (!clientDAO.exists(client.getClientID())) {
            clientDAO.save(client);
        } else {
            throw new ServiceException(AuthServiceEventLogId.ClientAlreadyExists, client.getClientID());
        }
    }

    @Override
    public void deleteClient(final String clientID) {
        if (clientDAO.exists(clientID)) {
            clientDAO.delete(clientID);
        } else {
            throw new ServiceException(AuthServiceEventLogId.ClientNotFound, clientID);
        }
    }

    public void setClientDAO(final IClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
