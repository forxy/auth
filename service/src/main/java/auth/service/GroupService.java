package auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import auth.db.dao.IGroupDAO;
import auth.exceptions.AuthServiceEventLogId;
import auth.api.v1.pojo.Group;
import common.exceptions.ServiceException;
import common.pojo.EntityPage;
import common.pojo.SortDirection;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation class for GroupService business logic
 */
public class GroupService implements IGroupService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private IGroupDAO groupDAO;

    public List<Group> getAllGroups() {
        List<Group> allGroups = new LinkedList<>();
        for (Group group : groupDAO.findAll()) {
            allGroups.add(group);
        }
        return allGroups;
    }

    @Override
    public EntityPage<Group> getGroups(final Integer page, final Integer size, final SortDirection sortDirection,
                                       final String sortedBy, final Group filter) {
        if (page >= 1) {
            int pageSize = size == null ? DEFAULT_PAGE_SIZE : size;
            PageRequest pageRequest;
            if (sortDirection != null && sortedBy != null) {
                Sort.Direction dir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
                pageRequest = new PageRequest(page - 1, pageSize, dir, sortedBy);
            } else {
                pageRequest = new PageRequest(page - 1, pageSize);
            }
            final Page<Group> p = groupDAO.findAll(pageRequest, filter);
            return new EntityPage<>(p.getContent(), p.getSize(), p.getNumber(), p.getTotalElements());
        } else {
            throw new ServiceException(AuthServiceEventLogId.InvalidPageNumber, page);
        }
    }

    @Override
    public List<Group> getGroups(List<String> groupCodes) {
        return groupDAO.findAll(groupCodes);
    }

    @Override
    public Group getGroup(final String groupCode) {
        Group group = groupDAO.findOne(groupCode);
        if (group == null) {
            throw new ServiceException(AuthServiceEventLogId.GroupNotFound, groupCode);
        }
        return group;
    }

    @Override
    public void updateGroup(final Group group) {
        if (groupDAO.exists(group.getCode())) {
            groupDAO.save(group);
        } else {
            throw new ServiceException(AuthServiceEventLogId.GroupNotFound, group.getCode());
        }
    }

    @Override
    public void createGroup(final Group group) {
        group.setCreateDate(new Date());
        if (!groupDAO.exists(group.getCode())) {
            groupDAO.save(group);
        } else {
            throw new ServiceException(AuthServiceEventLogId.GroupAlreadyExists, group.getCode());
        }
    }

    @Override
    public void deleteGroup(final String groupCode) {
        if (groupDAO.exists(groupCode)) {
            groupDAO.delete(groupCode);
        } else {
            throw new ServiceException(AuthServiceEventLogId.GroupNotFound, groupCode);
        }
    }

    public void setGroupDAO(final IGroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }
}
