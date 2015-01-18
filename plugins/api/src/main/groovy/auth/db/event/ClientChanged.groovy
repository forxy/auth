package auth.db.event

import auth.api.v1.Client

/**
 * Event when client has been updated
 */
class ClientChanged {
    Client from
    Client to
}
