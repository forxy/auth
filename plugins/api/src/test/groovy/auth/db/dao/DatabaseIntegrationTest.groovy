package auth.db.dao

import auth.api.v1.Group
import auth.api.v1.User
import common.status.api.ComponentStatus
import common.status.api.StatusType
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

    @Test
    void testGetUsers() {
        List<User> users = userDAO.findAll()
        assert users, 'Cannot find any users'
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
