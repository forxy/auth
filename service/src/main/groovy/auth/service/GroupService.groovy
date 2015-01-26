package auth.service

import auth.api.v1.Group
import auth.db.dao.IGroupDAO
import auth.db.exceptions.AuthDBEvent
import auth.exceptions.AuthEvent
import common.api.EntityPage
import common.api.SortDirection
import common.exceptions.ServiceException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Implementation class for user groups management
 */
class GroupService implements IGroupService {

    static final int DEFAULT_PAGE_SIZE = 10

    IGroupDAO groupDAO

    List<Group> getAllGroups() { groupDAO.findAll() }

    @Override
    EntityPage<Group> getGroups(final Integer page, final Integer size, final SortDirection sortDirection,
                                final String sortedBy, final Group filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size
            PageRequest pageRequest
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy)
            } else {
                pageRequest = new PageRequest(page - 1, pageSize)
            }
            final Page<Group> p = groupDAO.find pageRequest, filter
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, page)
        }
    }

    @Override
    List<Group> getGroups(Set<String> groupCodes) { groupDAO.find groupCodes }

    @Override
    Group getGroup(final String groupCode) {
        Group group = groupDAO.find(groupCode)
        if (group == null) {
            throw new ServiceException(AuthDBEvent.GroupNotFound, groupCode)
        }
        return group
    }

    @Override
    void updateGroup(final Group group) { groupDAO.save group }

    @Override
    void createGroup(final Group group) { groupDAO.create group }

    @Override
    void deleteGroup(final String groupCode) { groupDAO.delete groupCode }
}
