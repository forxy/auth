package auth.db.dao

import auth.api.v1.Client
import auth.api.v1.Group
import auth.api.v1.User
import spock.lang.Specification

import javax.annotation.Resource

/**
 * Auth service Data Sources integration test
 */
abstract class DatabaseIntegrationTest extends Specification {

    @Resource
    IClientDAO clientDAO
    @Resource
    IUserDAO userDAO
    @Resource
    IGroupDAO groupDAO
    @Resource
    IPermissionDAO permissionDAO

    def "delete client: granted and approved permissions should be removed cascade"() {
        setup:
        Client client = createClient 'testClient1'
        Client resource = createClient 'testResource1', ['rs1.test1', 'rs1.test2']
        User owner = createUser 'testUser1'
        Group group = createGroup 'testGroup1'

        when:
        permissionDAO.grantPermissionsToGroup(group.code, ['rs1.test1'] as Set)
        permissionDAO.approveAccountPermissions(owner.email, client.clientID, ['rs1.test1'] as Set)

        then:
        permissionDAO.getGroupPermissions(group.code) == ['rs1.test1'] as Set
        permissionDAO.getAccountApprovals(owner.email, client.clientID) == ['rs1.test1'] as Set

        when:
        delete client

        then:
        permissionDAO.getGroupPermissions(group.code) == ['rs1.test1'] as Set
        !permissionDAO.getAccountApprovals(owner.email, client.clientID)

        cleanup:
        delete resource
        delete group
        delete owner
    }

    def "delete resource: granted and approved permissions should be removed cascade"() {
        setup:
        Client client = createClient 'testClient2'
        Client resource = createClient 'testResource2', ['rs2.test1', 'rs2.test2']
        User owner = createUser 'testUser2'
        Group group = createGroup 'testGroup2'

        when:
        permissionDAO.grantPermissionsToGroup(group.code, ['rs2.test1'] as Set)
        permissionDAO.approveAccountPermissions(owner.email, client.clientID, ['rs2.test1'] as Set)

        then:
        permissionDAO.getGroupPermissions(group.code) == ['rs2.test1'] as Set
        permissionDAO.getAccountApprovals(owner.email, client.clientID) == ['rs2.test1'] as Set

        when:
        delete resource

        then:
        !permissionDAO.getGroupPermissions(group.code)
        !permissionDAO.getAccountApprovals(owner.email, client.clientID)

        cleanup:
        delete owner
        delete group
        delete client
    }

    def "delete group: granted and approved permissions should be removed cascade"() {
        setup:
        Client client = createClient 'testClient3'
        Client resource = createClient 'testResource3', ['rs3.test1', 'rs3.test2']
        User owner = createUser 'testUser3'
        Group group = createGroup 'testGroup3', [owner.email]

        when:
        permissionDAO.grantPermissionsToGroup(group.code, ['rs3.test1'] as Set)
        permissionDAO.approveAccountPermissions(owner.email, client.clientID, ['rs3.test1'] as Set)

        then:
        permissionDAO.getGroupPermissions(group.code) == ['rs3.test1'] as Set
        permissionDAO.getAccountApprovals(owner.email, client.clientID) == ['rs3.test1'] as Set

        when:
        delete group

        then:
        !permissionDAO.getGroupPermissions(group.code)
        !permissionDAO.getAccountApprovals(owner.email, client.clientID)

        cleanup:
        delete owner
        delete resource
        delete client
    }

    def "granted permission should not be removed when concurrent group still exist"() {
        setup:
        Client client = createClient 'testClient4'
        Client resource = createClient 'testResource4', ['rs4.read', 'rs4.write']
        User owner = createUser 'testOwner4'
        User user = createUser 'testUser4'
        Group adminGroup = createGroup 'testAdminGroup4', [owner.email]
        Group userGroup = createGroup 'testUserGroup4', [owner.email, user.email]

        when:
        // link groups with resource using permissions available for this client/resource
        permissionDAO.grantPermissionsToGroup(adminGroup.code, ['rs4.read', 'rs4.write'] as Set)
        permissionDAO.grantPermissionsToGroup(userGroup.code, ['rs4.read'] as Set)
        // approve these permissions for the group users
        permissionDAO.approveAccountPermissions(owner.email, client.clientID, ['rs4.read', 'rs4.write'] as Set)
        permissionDAO.approveAccountPermissions(user.email, client.clientID, ['rs4.read'] as Set)

        then:
        permissionDAO.getGroupPermissions(adminGroup.code) == ['rs4.read', 'rs4.write'] as Set
        permissionDAO.getGroupPermissions(userGroup.code) == ['rs4.read'] as Set

        permissionDAO.getAccountApprovals(owner.email, client.clientID) == ['rs4.read', 'rs4.write'] as Set
        permissionDAO.getAccountApprovals(user.email, client.clientID) == ['rs4.read'] as Set

        when:
        delete adminGroup

        then:
        !permissionDAO.getGroupPermissions(adminGroup.code)
        permissionDAO.getAccountApprovals(owner.email, client.clientID) == ['rs4.read'] as Set
        permissionDAO.getAccountApprovals(user.email, client.clientID) == ['rs4.read'] as Set

        cleanup:
        delete userGroup
        delete owner
        delete user
        delete resource
        delete client
    }

    //------------------------------------------------
    // Utils
    //------------------------------------------------

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
