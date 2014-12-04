package auth.service

import auth.api.v1.User
import common.api.EntityPage
import common.api.SortDirection

/**
 * Entry point into user service business logic
 */
interface IUserService {

    Iterable<User> getAllUsers()

    EntityPage<User> getUsers(final Integer page, final Integer size, final SortDirection sortDirection,
                              final String sortedBy, final User filter)

    User getUser(final String email)

    void updateUser(final User user)

    User createUser(final User user)

    void deleteUser(final String email)
}
