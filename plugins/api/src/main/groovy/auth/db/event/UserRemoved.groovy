package auth.db.event

import auth.api.v1.User

/**
 * Event when user has been removed
 */
class UserRemoved {
    User user
}
