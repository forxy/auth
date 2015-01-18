package auth.db.event

import auth.api.v1.Group

/**
 * Event when group has been updated
 */
class GroupChanged {
    Group from
    Group to
}
