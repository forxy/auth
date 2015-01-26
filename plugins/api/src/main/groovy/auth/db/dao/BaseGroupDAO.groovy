package auth.db.dao

import auth.api.v1.Group
import auth.db.event.UserChanged
import auth.db.event.UserRemoved
import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

/**
 * Performs common User DB actions
 */
abstract class BaseGroupDAO implements IGroupDAO {

    IUserDAO userDAO
    EventBus eventBus

    @Subscribe
    @AllowConcurrentEvents
    void updateUserMembership(UserChanged userChanged) {
        Set<String> oldGroups = userChanged.from?.groups ?: []
        Set<String> newGroups = userChanged.to?.groups ?: []
        Set<String> groupsToRemove = oldGroups - newGroups
        Set<String> groupsToAdd = newGroups - oldGroups
        find(groupsToRemove).each {
            it.members -= userChanged.to?.email
            silentSave it
        }
        find(groupsToAdd).each {
            it.members << userChanged.to?.email
            silentSave it
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    void updateUserMembership(UserRemoved userRemoved) {
        userRemoved?.user?.groups?.each {
            Group group = find it
            group.members -= userRemoved.user.email
            silentSave group
        }
    }

    protected void mapNewUsers(final Group group) {
        if (group.members) {
            userDAO?.find(group.members)?.each {
                it.groups << group.code
                userDAO?.silentSave it
            }
        }
    }

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
        eventBus.register this
    }
}
