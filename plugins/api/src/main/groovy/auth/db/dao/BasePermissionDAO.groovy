package auth.db.dao

import auth.db.event.ClientRemoved
import auth.db.event.GroupRemoved
import auth.db.event.UserRemoved
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

/**
 * Performs common Permission related DB actions
 */
abstract class BasePermissionDAO implements IPermissionDAO {

    EventBus eventBus

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
        eventBus.register this
    }

    @Subscribe
    void handleClientRemoved(ClientRemoved clientRemovedEvent) {
        deleteClientPermissions(clientRemovedEvent?.client?.clientID)
        deleteAccountPermissions(clientRemovedEvent?.client?.email)
    }

    @Subscribe
    void handleGroupRemoved(GroupRemoved groupRemovedEvent) {
        deleteGroupPermissions(groupRemovedEvent?.group?.code)
    }

    @Subscribe
    void handleUserRemoved(UserRemoved userRemovedEvent) {
        deleteAccountPermissions(userRemovedEvent?.user?.email)
    }
}
