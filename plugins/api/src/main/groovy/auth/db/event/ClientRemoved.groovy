package auth.db.event

import auth.api.v1.Client

/**
 * Event client scope has been removed
 */
class ClientRemoved {
    Client client
}
