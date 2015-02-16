package auth.db.dao

import auth.api.v1.Group
import auth.api.v1.User
import common.status.api.ComponentStatus
import common.status.api.StatusType
import spock.lang.Specification

import javax.annotation.Resource

/**
 * User Data Source tests
 */
abstract class UserAndGroupDAOTest extends Specification {

    protected static final ADMIN_EMAIL = 'admin@forxy.ru'

    @Resource
    IUserDAO userDAO
    @Resource
    IGroupDAO groupDAO

    def "user DAO should return at least one user"() {
        expect:
        userDAO.findAll()
    }

    def "group DAO should return at least one group"() {
        expect:
        groupDAO.findAll()
    }

    def "group DAO should have group 'users'"() {
        expect:
        groupDAO.find 'users'
    }

    def "user DAO should authenticate a new User"() {
        setup:
        User user = createUser 'test'

        expect:
        userDAO.authenticate user.login, 'password'

        cleanup:
        delete user
    }

    def "user DAO should create user"() {
        setup:
        User user = createUser 'test'

        expect:
        user.login == 'test'
        user.email == 'test@mail.com'
        user.firstName == 'testFirstName'
        user.lastName == 'testLastName'
        user.password != 'password'

        userDAO.find user.login
        userDAO.find user.email

        cleanup:
        delete user
    }

    def "user DAO should update user with new name"() {
        setup:
        User user = createUser 'test'

        expect:
        user

        when:
        user.firstName = 'otherTestFirstName'
        user = userDAO.save user

        then:
        user.firstName == 'otherTestFirstName'

        cleanup:
        delete user
    }

    void "group DAO should create group"() {
        setup:
        Group group = createGroup 'test'

        expect:
        group.code == 'test'
        group.name == 'Test Group'
        group.description == 'Test Group'
        groupDAO.find group.code

        cleanup:
        delete group
    }

    def "user DAO should return GREEN system status"() {
        given:
        ComponentStatus status = userDAO.status

        expect:
        status
        status.componentType == ComponentStatus.ComponentType.DB
        status.status == StatusType.GREEN
        status.name == 'User DAO'
    }

    def "group DAO should return GREEN system status"() {
        given:
        ComponentStatus status = groupDAO.status

        expect:
        status
        status.componentType == ComponentStatus.ComponentType.DB
        status.status == StatusType.GREEN
        status.name == 'Group DAO'
    }

    def "new Group should include itself into its users groups"() {
        setup:
        User user = createUser 'test'
        Group group = createGroup 'test', [user.email]

        when:
        user = userDAO.find user.email
        group = groupDAO.find group.code

        then:
        group.code in user.groups
        user.email in group.members

        cleanup:
        delete group
        delete user
    }


    def "new User should include itself into its groups members"() {
        setup:
        Group group = createGroup 'test'
        User user = createUser 'test', [group.code]

        when:
        user = userDAO.find user.email
        group = groupDAO.find group.code

        then:
        group.code in user.groups
        user.email in group.members

        cleanup:
        delete user
        delete group
    }

    def "groups should update users membership after users update"() {
        setup:
        Group group1 = createGroup 'test1'
        Group group2 = createGroup 'test2'
        Group group3 = createGroup 'test3'
        User user1 = createUser 'test1', [group1.code, group2.code]
        User user2 = createUser 'test2', [group2.code, group3.code]


        when:
        user1 = userDAO.find user1.email
        user2 = userDAO.find user2.email
        group1 = groupDAO.find group1.code
        group2 = groupDAO.find group2.code
        group3 = groupDAO.find group3.code

        then:
        [ADMIN_EMAIL, user1.email] as Set == group1.members
        [ADMIN_EMAIL, user1.email, user2.email] as Set == group2.members
        [ADMIN_EMAIL, user2.email] as Set == group3.members


        when:
        user1.groups -= group1.code
        user1.groups << group3.code
        userDAO.save user1
        user1 = userDAO.find user1.email
        group1 = groupDAO.find group1.code
        group2 = groupDAO.find group2.code
        group3 = groupDAO.find group3.code

        then:
        [ADMIN_EMAIL] as Set == group1.members
        [ADMIN_EMAIL, user1.email, user2.email] as Set == group2.members
        [ADMIN_EMAIL, user1.email, user2.email] as Set == group3.members
        [group2.code, group3.code] as Set == user1.groups


        cleanup:
        delete user2
        delete user1
        delete group3
        delete group2
        delete group1
    }

    def "user should delete it self from groups membership"() {
        setup:
        Group group = createGroup 'test'
        User user = createUser 'test', [group.code]

        when:
        delete user
        group = groupDAO.find group.code

        then:
        !(user.email in group.members)

        cleanup:
        delete group
    }

    def "group should delete it self from users groups"() {
        setup:
        User user = createUser 'test'
        Group group = createGroup 'test', [user.email]

        when:
        delete group
        user = userDAO.find user.email

        then:
        !(group.code in user.groups)

        cleanup:
        delete user
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
        assert !userDAO.find(user.email), "User '$user.email' has not been removed"
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
}
