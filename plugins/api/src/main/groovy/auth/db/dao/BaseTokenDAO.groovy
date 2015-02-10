package auth.db.dao

import auth.db.event.ClientRemoved
import auth.db.event.UserRemoved
import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

/**
 * Performs common Tokens related DB processing
 */
abstract class BaseTokenDAO implements ITokenDAO {

    EventBus eventBus

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
        eventBus.register this
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleClientRemoved(ClientRemoved clientRemovedEvent) {
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleUserRemoved(UserRemoved userRemovedEvent) {
    }
}
