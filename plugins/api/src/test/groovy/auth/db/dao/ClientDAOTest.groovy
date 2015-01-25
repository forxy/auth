package auth.db.dao

import auth.api.v1.Client
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * Client Data Source tests
 */
abstract class ClientDAOTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    IClientDAO clientDAO

    @Test
    void testGetClients() {
        List<Client> clients = clientDAO.findAll()
        assert clients, 'Cannot find any clients'
    }

    @Test
    void testCreateClient() {
        Client client = createClient 'test'
        try {
            client = clientDAO.find client.clientID
            assert client.name == 'test'
            assert client.description == 'test client'
            assert client.webUri == 'http://testClient'
            assert client.scopes == [] as Set
        } finally {
            delete client
        }
    }

    @Test
    void testUpdateClient() {
        Client client = createClient 'test'
        try {
            assert client, 'Test client has not been added'
            client.name = 'test 2'
            clientDAO.save client
            client = clientDAO.find client.clientID
            assert client.name == 'test 2'
        } finally {
            delete client
        }
    }

    @Test
    void testAuthenticate() {
        Client client = createClient 'test'
        try {
            client = clientDAO.authenticate client.clientID, 'password'
            assert client, 'Unable to authenticate test client'
        } finally {
            delete client
        }
    }

    @Test
    void testGetUserDAOSystemStatus() {
        ComponentStatus status = clientDAO.status
        assert status, 'DB status not available'
        assert status.componentType == ComponentStatus.ComponentType.DB
        //assert status.status == StatusType.GREEN
        assert status.name == 'Client DAO'
    }

    Client createClient(String login) {
        createClient login, null, null, null
    }

    Client createClient(String login, List<String> scopes) {
        createClient login, scopes, null, null
    }

    Client createClient(String login, List<String> scopes, List<String> redirectUris) {
        createClient login, scopes, redirectUris, null
    }

    Client createClient(String name, List<String> scopes, List<String> redirectUris, List<String> audiences) {
        Client client = clientDAO.create(new Client(
                clientID: UUID.randomUUID().toString(),
                name: name,
                password: 'password',
                description: 'test client',
                webUri: 'http://testClient',
                scopes: scopes,
                redirectUris: redirectUris,
                audiences: audiences
        ))
        assert client, "Client '$name' has not been added"
        return client
    }

    void delete(Client client) {
        clientDAO.delete client
        assert !clientDAO.find(client.clientID), "Client '$client.name' has not been removed"
    }
}
