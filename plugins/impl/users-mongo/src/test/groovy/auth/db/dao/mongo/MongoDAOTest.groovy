package auth.db.dao.mongo

import auth.db.dao.UserAndGroupDAOTest
import org.springframework.test.context.ContextConfiguration

/**
 * User Data Source tests for MongoDB implementation
 */
@ContextConfiguration(locations = ['classpath:spring/spring-test-context.xml'])
class MongoDAOTest extends UserAndGroupDAOTest {

    /*@Test
    void testSaveUser() {
        userDAO.delete('test')
        User user = userDAO.create(new User(
                login: 'test',
                email: "test@mail.com",
                password: 'password',
                gender: Gender.MALE,
                birthDate: DateTime.parse('1987-01-03'),
                firstName: 'testFirstName',
                lastName: 'testLastName',
                groups: ['test1', 'test2']
        ))
        user = userDAO.find(user.login)
        assert user, "User hasn't been created"
        assert user.groups, "User groups haven't been mapped"
        userDAO.delete('test')
    }

    @Test
    void testSaveGroup() {
        groupDAO.delete('test')
        UserGroup group = groupDAO.create(new UserGroup(
                code: 'test',
                name: "Test Group",
                description: 'Some Test Group',
                members: ['test1', 'test2']
        ))
        group = groupDAO.find(group.code)
        assert group, "Group hasn't been created"
        assert group.members, "Group members haven't been mapped"
        groupDAO.delete('test')
    }*/
}
