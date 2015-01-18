package auth.db.dao

import auth.api.v1.User
import auth.db.event.GroupChanged
import auth.db.event.GroupRemoved
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

/**
 * Performs common User DB actions
 */
abstract class BaseUserDAO implements IUserDAO {

    IGroupDAO groupDAO
    EventBus eventBus

    @Subscribe
    void updateGroupMembership(GroupChanged groupChanged) {
        Set<String> oldUsers = groupChanged.from?.members ?: []
        Set<String> newUsers = groupChanged.to?.members ?: []
        Set<String> usersToRemove = oldUsers - newUsers
        Set<String> usersToAdd = newUsers - oldUsers
        find(usersToRemove).each {
            it.groups -= groupChanged.to?.code
            silentSave it
        }
        find(usersToAdd).each {
            it.groups << groupChanged.to?.code
            silentSave it
        }
    }

    @Subscribe
    void updateGroupMembership(GroupRemoved groupRemoved) {
        groupRemoved?.group?.members?.each {
            User user = find it
            user.groups -= groupRemoved.group.code
            silentSave user
        }
    }

    protected void mapNewGroups(final User user) {
        if (user.groups) {
            groupDAO.find(user.groups)?.each {
                it.members << user.login
                groupDAO.silentSave it
            }
        }
    }

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus
        eventBus.register this
    }
}
