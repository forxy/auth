package auth.service

import auth.api.v1.Group
import common.api.EntityPage
import common.api.SortDirection

/**
 * Entry point into auth service business logic
 */
interface IGroupService {

    Iterable<Group> getAllGroups()

    EntityPage<Group> getGroups(final Integer page, final Integer size, final SortDirection sortDirection,
                                final String sortedBy, final Group filter)

    List<Group> getGroups(Set<String> groupCodes)

    Group getGroup(final String groupCode)

    void updateGroup(final Group auth)

    void createGroup(final Group auth)

    void deleteGroup(final String groupCode)
}
