package auth.db.dao

import com.google.common.eventbus.EventBus

/**
 * Performs common User DB actions
 */
abstract class BaseClientDAO implements IClientDAO {

    EventBus eventBus

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
        eventBus.register this
    }
}
