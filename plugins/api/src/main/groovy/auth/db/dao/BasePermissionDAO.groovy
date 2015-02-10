package auth.db.dao

import auth.db.event.ClientChanged
import auth.db.event.ClientRemoved
import auth.db.event.GroupRemoved
import auth.db.event.UserRemoved
import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

/**
 * Performs common Permission related DB processing
 */
abstract class BasePermissionDAO implements IPermissionDAO {

    EventBus eventBus

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
        eventBus.register this
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleClientRemoved(ClientRemoved clientRemovedEvent) {
        revokePermissions clientRemovedEvent?.client?.scopes
        deleteAccountPermissions clientRemovedEvent?.client?.email
        revokeApprovals clientRemovedEvent?.client?.scopes
        deleteClientApprovals clientRemovedEvent?.client?.clientID
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleClientChanged(ClientChanged clientChangedEvent) {
        revokePermissions clientChangedEvent.to?.scopes - clientChangedEvent.from?.scopes
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleGroupRemoved(GroupRemoved groupRemovedEvent) {
        deleteGroupPermissions groupRemovedEvent?.group
    }

    @Subscribe
    @AllowConcurrentEvents
    void handleUserRemoved(UserRemoved userRemovedEvent) {
        deleteAccountPermissions userRemovedEvent?.user?.email
        deleteOwnerApprovals userRemovedEvent?.user?.email
    }
}
