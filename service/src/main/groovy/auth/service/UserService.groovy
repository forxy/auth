package auth.service

import auth.api.v1.User
import auth.db.dao.IUserDAO
import auth.db.exceptions.AuthDBEvent
import auth.exceptions.AuthEvent
import common.api.EntityPage
import common.api.SortDirection
import common.exceptions.ServiceException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Implementation class for users management
 */
class UserService implements IUserService {

    static final int DEFAULT_PAGE_SIZE = 10

    IUserDAO userDAO

    List<User> getAllUsers() { userDAO.findAll() }

    @Override
    EntityPage<User> getUsers(final Integer pageNumber, final Integer size, final SortDirection sortDirection,
                              final String sortedBy, final User filter) {
        if (pageNumber >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(pageNumber - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(pageNumber - 1, pageSize)
            }
            final Page<User> page = userDAO.find pageRequest, filter
            return new EntityPage<>(page.content, page.size, page.number, page.totalElements)
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, pageNumber)
        }
    }

    @Override
    User getUser(final String email) {
        User user = userDAO.find email
        if (user == null) {
            throw new ServiceException(AuthDBEvent.UserNotFound, email)
        }
        return user
    }

    @Override
    void updateUser(final User user) { userDAO.save user }

    @Override
    User createUser(final User user) { userDAO.create user }

    @Override
    void deleteUser(final String email) { userDAO.delete email }
}
