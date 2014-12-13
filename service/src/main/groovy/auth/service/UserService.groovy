package auth.service

import auth.api.v1.User
import auth.db.dao.IUserDAO
import auth.exceptions.AuthEvent
import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Implementation class for UserService business logic
 */
class UserService implements IUserService {

    static final int DEFAULT_PAGE_SIZE = 10

    IUserDAO userDAO

    PasswordEncoder passwordEncoder

    List<User> getAllUsers() {
        return userDAO.findAll().collect { it }
    }

    @Override
    EntityPage<User> getUsers(final Integer page, final Integer size, final SortDirection sortDirection,
                              final String sortedBy, final User filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(page - 1, pageSize)
            }
            final Page<User> p = userDAO.findAll(pageRequest, filter)
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, page)
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
        if (userDAO.exists(user.getEmail())) {
            userDAO.save(user)
        } else {
            throw new ServiceException(AuthEvent.UserNotFound, user.getEmail())
        }
    }

    @Override
    User createUser(final User user) {
        if (!userDAO.exists(user.getEmail())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()))
            return userDAO.save(user)
        } else {
            throw new ServiceException(AuthEvent.UserAlreadyExists, user.getEmail())
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
