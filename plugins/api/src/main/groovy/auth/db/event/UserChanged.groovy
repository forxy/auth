package auth.db.event

import auth.api.v1.User

/**
 * Event when user has been updated
 */
class UserChanged {
    User from
    User to
}
