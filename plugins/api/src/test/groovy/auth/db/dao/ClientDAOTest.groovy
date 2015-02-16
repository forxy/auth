package auth.db.dao

import auth.api.v1.Client
import common.status.api.ComponentStatus
import common.status.api.StatusType
import spock.lang.Specification

import javax.annotation.Resource

/**
 * Client Data Source tests
 */
abstract class ClientDAOTest extends Specification {

    @Resource
    IClientDAO clientDAO

    def "client DAO should return at least one client"() {
        expect:
        clientDAO.findAll()
    }

    def "client DAO should authenticate a new Client"() {
        setup:
        Client client = createClient 'test'

        expect:
        clientDAO.authenticate client.email, 'password'

        cleanup:
        delete client
    }

    def "client DAO should create client"() {
        setup:
        Client client = createClient 'test'

        expect:
        client.name == 'test'
        client.description == 'test client'
        client.webUri == 'http://testClient'
        !client.scopes
        client.password != 'password'

        clientDAO.find client.clientID

        cleanup:
        delete client
    }

    def "client DAO should update client with new name"() {
        setup:
        Client client = createClient 'test'

        expect:
        client

        when:
        client.name = 'test 2'
        client = clientDAO.save client

        then:
        client.name == 'test 2'

        cleanup:
        delete client
    }

    def "client DAO should return GREEN system status"() {
        given:
        ComponentStatus status = clientDAO.status

        expect:
        status
        status.componentType == ComponentStatus.ComponentType.DB
        status.status == StatusType.GREEN
        status.name == 'Client DAO'
    }


    //------------------------------------------------
    // Utils
    //------------------------------------------------

    Client createClient(String name) {
        createClient name, null, null, null
    }

    Client createClient(String name, List<String> scopes) {
        createClient name, scopes, null, null
    }

    Client createClient(String name, List<String> scopes, List<String> redirectUris) {
        createClient name, scopes, redirectUris, null
    }

    Client createClient(String name, List<String> scopes, List<String> redirectUris, List<String> audiences) {
        String id = UUID.randomUUID().toString()
        Client client = clientDAO.create(new Client(
                clientID: id,
                email: "$id@forxy.ru",
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
