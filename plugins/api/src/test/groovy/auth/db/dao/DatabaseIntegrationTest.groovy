package auth.db.dao

import auth.api.v1.Client
import auth.api.v1.Group
import auth.api.v1.User
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * Auth service Data Sources integration test
 */
abstract class DatabaseIntegrationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    IClientDAO clientDAO
    @Autowired
    IUserDAO userDAO
    @Autowired
    IGroupDAO groupDAO
    @Autowired
    IPermissionDAO permissionDAO

    @Test
    void testDeleteClientWithGrantedAndApprovedPermissions() {
        Client client = createClient 'testClient'
        Client resource = createClient 'testResource', ['test1', 'test2']
        User owner = createUser 'testUser'
        Group group = createGroup 'testGroup'
        try {
            permissionDAO.grantPermissionsToGroup(group.code, resource.clientID, ['test1'] as Set)
            permissionDAO.approveAccountPermissions(owner.email, client.clientID, resource.clientID, ['test1'] as Set)
            assert permissionDAO.getGroupPermissions(group.code, resource.clientID) == ['test1'] as Set, 'Permissions not granted'
            assert permissionDAO.getAccountApprovals(owner.email, client.clientID, resource.clientID) == ['test1'] as Set, 'Permissions not approved'

            delete client

            assert permissionDAO.getGroupPermissions(group.code, resource.clientID) == ['test1'] as Set, 'Permissions should be still granted'
            assert !permissionDAO.getAccountApprovals(owner.email, client.clientID, resource.clientID), 'Approved client permissions not removed'
        } finally {
            delete resource
            delete group
            delete owner
        }
    }

    @Test
    void testDeleteResourceWithGrantedAndApprovedPermissions() {
        Client client = createClient 'testClient'
        Client resource = createClient 'testResource', ['test1', 'test2']
        User owner = createUser 'testUser'
        Group group = createGroup 'testGroup'
        try {
            permissionDAO.grantPermissionsToGroup(group.code, resource.clientID, ['test1'] as Set)
            permissionDAO.approveAccountPermissions(owner.email, client.clientID, resource.clientID, ['test1'] as Set)
            assert permissionDAO.getGroupPermissions(group.code, resource.clientID) == ['test1'] as Set, 'Permissions not granted'
            assert permissionDAO.getAccountApprovals(owner.email, client.clientID, resource.clientID) == ['test1'] as Set, 'Permissions not approved'

            delete resource

            assert !permissionDAO.getGroupPermissions(group.code, resource.clientID), 'Permissions are still granted'
            assert !permissionDAO.getAccountApprovals(owner.email, client.clientID, resource.clientID), 'Approved client permissions not removed'
        } finally {
            delete client
            delete group
            delete owner
        }
    }

    @Test
    void testDeleteGroupWithGrantedAndApprovedPermissions() {
        Client client = createClient 'testClient'
        Client resource = createClient 'testResource', ['test1', 'test2']
        User owner = createUser 'testUser'
        Group group = createGroup 'testGroup'
        try {
            permissionDAO.grantPermissionsToGroup(group.code, resource.clientID, ['test1'] as Set)
            permissionDAO.approveAccountPermissions(owner.email, client.clientID, resource.clientID, ['test1'] as Set)
            assert permissionDAO.getGroupPermissions(group.code, resource.clientID) == ['test1'] as Set, 'Permissions not granted'
            assert permissionDAO.getAccountApprovals(owner.email, client.clientID, resource.clientID) == ['test1'] as Set, 'Permissions not approved'

            delete group

            assert !permissionDAO.getGroupPermissions(group.code, resource.clientID), 'Permissions are still granted'
            assert !permissionDAO.getAccountApprovals(owner.email, client.clientID, resource.clientID), 'Approved client permissions not removed'
        } finally {
            delete client
            delete group
            delete owner
        }
    }

    User createUser(String login) {
        createUser login, null
    }

    User createUser(String login, List<String> groups) {
        User user = userDAO.create(new User(
                login: login,
                email: "$login@mail.com",
                password: 'password',
                firstName: 'testFirstName',
                lastName: 'testLastName',
                groups: groups
        ))
        assert user, "User '$login' has not been added"
        return user
    }

    void delete(User user) {
        userDAO.delete user
        assert !userDAO.find(user.login), "User '$user.login' has not been removed"
    }

    Group createGroup(String code) {
        createGroup code, null
    }

    Group createGroup(String code, List<String> members) {
        Group group = groupDAO.create(new Group(
                code: code,
                name: 'Test Group',
                description: 'Test Group',
                members: members
        ))
        assert group, "Group '$code' has not been added"
        return group
    }

    void delete(Group group) {
        groupDAO.delete group
        assert !groupDAO.find(group.code), "Group '$group.code' has not been removed"
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
