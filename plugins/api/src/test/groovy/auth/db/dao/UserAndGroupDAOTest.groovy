package auth.db.dao

import auth.api.v1.Group
import auth.api.v1.User
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * User Data Source tests
 */
abstract class UserAndGroupDAOTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    IUserDAO userDAO
    @Autowired
    IGroupDAO groupDAO

    @Test
    void testGetUsers() {
        List<User> users = userDAO.findAll()
        assert users, 'Cannot find any users'
    }

    @Test
    void testGetGroup() {
        Group usersGroup = groupDAO.find 'users'
        assert usersGroup, 'Group "users" not found'
    }

    @Test
    void testGetAllGroups() {
        List<Group> userGroups = groupDAO.findAll()
        assert userGroups, 'Cannot find any groups'
    }

    @Test
    void testAuthenticate() {
        User admin = userDAO.authenticate 'admin', 'password'
        assert admin, 'Admin should not be null'
    }

    @Test
    void testCreateUser() {
        User user = createUser 'test'
        try {
            assert user.login == 'test'
            assert user.email == 'test@mail.com'
            assert user.firstName == 'testFirstName'
            assert user.lastName == 'testLastName'
            assert userDAO.authenticate('test', 'password'), 'Test user not authenticated'
        } finally {
            delete user
        }
    }

    @Test
    void testUpdateUser() {
        User user = createUser 'test'
        try {
            assert user, 'Test user has not been added'
            user.firstName = 'otherTestFirstName'
            user = userDAO.save user
            assert user.firstName == 'otherTestFirstName'
        } finally {
            delete user
        }
    }

    @Test
    void testCreateGroup() {
        Group group = createGroup 'test'
        try {
            assert group.code == 'test'
            assert group.name == 'Test Group'
            assert group.description == 'Test Group'
            assert groupDAO.find('test'), 'Test group not found'
        } finally {
            delete group
        }
    }

    @Test
    void testGetUserDAOSystemStatus() {
        ComponentStatus status = userDAO.status
        assert status, 'DB status not available'
        assert status.componentType == ComponentStatus.ComponentType.DB
        assert status.status == StatusType.GREEN
        assert status.name == 'User DAO'
    }

    @Test
    void testGetGroupDAOSystemStatus() {
        ComponentStatus status = groupDAO.status
        assert status, 'DB status not available'
        assert status.componentType == ComponentStatus.ComponentType.DB
        assert status.status == StatusType.GREEN
        assert status.name == 'Group DAO'
    }

    @Test
    void testCreateGroupWithUser() {
        User user = createUser 'test'
        try {
            Group group = createGroup 'test', [user.login]
            try {
                user = userDAO.find user.login
                assert group.code in user.groups, 'Group has not been mapped into users'

                group = groupDAO.find user.login
                assert user.login in group.members, 'User has not been includes into group'
            } finally {
                delete group
            }
        } finally {
            delete user
        }
    }

    @Test
    void testCreateUserWithGroup() {
        Group group = createGroup 'test'
        try {
            User user = createUser 'test', [group.code]
            try {
                user = userDAO.find(user.login)
                assert group.code in user.groups, 'Group has not been mapped into users'

                group = groupDAO.find(user.login)
                assert user.login in group.members, 'User has not been includes into group'
            } finally {
                delete user
            }
        } finally {
            delete group
        }
    }

    @Test
    void testUpdateUsersAndGroups() {
        Group group1 = createGroup 'test1'
        Group group2 = createGroup 'test2'
        Group group3 = createGroup 'test3'
        try {
            User user1 = createUser 'test1', [group1.code, group2.code]
            User user2 = createUser 'test2', [group2.code, group3.code]
            try {
                user1 = userDAO.find user1.login
                assert user1.groups, 'Groups have not been mapped into user #1'
                user2 = userDAO.find user2.login
                assert user2.groups, 'Groups have not been mapped into user #2'

                group1 = groupDAO.find group1.code
                group2 = groupDAO.find group2.code
                group3 = groupDAO.find group3.code
                assert ['admin', user1.login] as Set == group1.members, 'Group #1 should contain User #1'
                assert ['admin', user1.login, user2.login] as Set == group2.members, 'Group #2 should contain User #1 and #2'
                assert ['admin', user2.login] as Set == group3.members, 'Group #3 should contain User #2'

                user1.groups -= group1.code
                user1.groups << group3.code
                userDAO.save user1

                group1 = groupDAO.find group1.code
                group2 = groupDAO.find group2.code
                group3 = groupDAO.find group3.code
                assert ['admin'] as Set == group1.members, 'Group #1 should contain only admin'
                assert ['admin', user1.login, user2.login] as Set == group2.members, 'Group #2 should contain User #1 and #2'
                assert ['admin', user1.login, user2.login] as Set == group3.members, 'Group #3 should contain User #1 and #2'

                user1 = userDAO.find user1.login
                assert [group2.code, group3.code] as Set == user1.groups, 'User #1 should have Group #2 and #3'
            } finally {
                delete user1
                delete user2
            }
        } finally {
            delete group1
            delete group2
            delete group3
        }
    }

    @Test
    void testDeleteUserWithGroup() {
        Group group = createGroup 'test'
        try {
            User user = createUser 'test', [group.code]
            try {
                user = userDAO.find(user.login)
                assert group.code in user.groups, 'Group has not been mapped into users'

                group = groupDAO.find(user.login)
                assert user.login in group.members, 'User has not been includes into group'
            } finally {
                delete user
                group = groupDAO.find user.login
                assert !(user.login in group.members), "User $user.login hasn't been removed from the group members"
            }
        } finally {
            delete group
        }
    }

    @Test
    void testDeleteGroupWithUser() {
        User user = createUser 'test'
        try {
            Group group = createGroup 'test', [user.login]
            try {
                user = userDAO.find(user.login)
                assert group.code in user.groups, 'Group has not been mapped into users'

                group = groupDAO.find(user.login)
                assert user.login in group.members, 'User has not been includes into group'
            } finally {
                delete group
                user = userDAO.find user.login
                assert !(group.code in user.groups), "Group $group.code hasn't been removed from the user's groups"
            }
        } finally {
            delete user
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
}
