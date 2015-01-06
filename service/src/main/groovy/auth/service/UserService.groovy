package auth.service

import auth.api.v1.User
import auth.db.dao.IUserDAO
import auth.exceptions.AuthEvent
import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import org.joda.time.DateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Implementation class for UserService business logic
 */
class UserService implements IUserService {

    static final int DEFAULT_PAGE_SIZE = 10

    IUserDAO userDAO

    List<User> getAllUsers() {
        return userDAO.findAll().collect { it }
    }

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
            final Page<User> page = userDAO.findAll(pageRequest, filter)
            return new EntityPage<>(page.content, page.size, page.number, page.totalElements)
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, pageNumber)
        }
    }

    @Override
    User getUser(final String email) {
        User user = userDAO.findOne(email)
        if (user == null) {
            throw new ServiceException(AuthEvent.UserNotFound, email)
        }
        return user
    }

    @Override
    void updateUser(final User user) {
        if (userDAO.exists(user.email)) {
            user.updateDate = DateTime.now()
            userDAO.save(user)
        } else {
            throw new ServiceException(AuthEvent.UserNotFound, user.email)
        }
    }

    @Override
    User createUser(final User user) {
        if (!userDAO.exists(user.email)) {
            user.createDate = DateTime.now()
            user.updateDate = DateTime.now()
            return userDAO.save(user)
        } else {
            throw new ServiceException(AuthEvent.UserAlreadyExists, user.email)
        }
    }

    @Override
    void deleteUser(final String email) {
        if (userDAO.exists(email)) {
            userDAO.delete(email)
        } else {
            throw new ServiceException(AuthEvent.UserNotFound, email)
        }
    }
}
