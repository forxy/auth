package auth.service

import auth.api.v1.Group
import auth.db.dao.IGroupDAO
import auth.exceptions.AuthEvent
import common.exceptions.ServiceException
import common.api.EntityPage
import common.api.SortDirection
import org.joda.time.DateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Implementation class for GroupService business logic
 */
class GroupService implements IGroupService {

    static final int DEFAULT_PAGE_SIZE = 10

    IGroupDAO groupDAO

    List<Group> getAllGroups() {
        groupDAO.findAll().collect { it }
    }

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
            final Page<Group> p = groupDAO.findAll(pageRequest, filter)
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements())
        } else {
            throw new ServiceException(AuthEvent.InvalidPageNumber, page)
        }
    }

    @Override
    List<Group> getGroups(List<String> groupCodes) {
        return groupDAO.findAll(groupCodes)
    }

    @Override
    Group getGroup(final String groupCode) {
        Group group = groupDAO.findOne(groupCode)
        if (group == null) {
            throw new ServiceException(AuthEvent.GroupNotFound, groupCode)
        }
        return group
    }

    @Override
    void updateGroup(final Group group) {
        if (groupDAO.exists(group.getCode())) {
            group.updateDate = DateTime.now()
            groupDAO.save(group)
        } else {
            throw new ServiceException(AuthEvent.GroupNotFound, group.getCode())
        }
    }

    @Override
    void createGroup(final Group group) {
        if (!groupDAO.exists(group.getCode())) {
            group.createDate = DateTime.now()
            group.updateDate = DateTime.now()
            groupDAO.save(group)
        } else {
            throw new ServiceException(AuthEvent.GroupAlreadyExists, group.getCode())
        }
    }

    @Override
    void deleteGroup(final String groupCode) {
        if (groupDAO.exists(groupCode)) {
            groupDAO.delete(groupCode)
        } else {
            throw new ServiceException(AuthEvent.GroupNotFound, groupCode)
        }
    }
}
