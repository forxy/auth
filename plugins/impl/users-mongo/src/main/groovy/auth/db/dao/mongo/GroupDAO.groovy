package auth.db.dao.mongo

import auth.api.v1.Group
import auth.db.dao.BaseGroupDAO
import auth.db.dao.mongo.domain.GroupDTO
import auth.db.event.GroupChanged
import auth.db.event.GroupRemoved
import auth.db.event.UserRemoved
import auth.db.exceptions.AuthDBEvent
import com.google.common.eventbus.Subscribe
import common.exceptions.ServiceException
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.joda.time.DateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

import static GroupDTO.fromDomain
import static GroupDTO.toDomain

/**
 * Mongo DB based data source for groups
 */
class GroupDAO extends BaseGroupDAO {

    String adminLoginName

    MongoTemplate mongoTemplate

    @Override
    List<Group> findAll() {
        return fromDomain(mongoTemplate.findAll(GroupDTO.class))
    }

    @Override
    List<Group> find(final Sort sort) {
        return fromDomain(mongoTemplate.find(Query.query(new Criteria()).with(sort), GroupDTO.class))
    }

    @Override
    Page<Group> find(final Pageable pageable) {
        return new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), GroupDTO.class),
                pageable, count()
        )
    }

    @Override
    Page<Group> find(final Pageable pageable, final Group filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.code)) {
                query.addCriteria(new Criteria('_id').regex(filter.code, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.name)) {
                query.addCriteria(new Criteria('name').regex(filter.name, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.updatedBy)) {
                query.addCriteria(new Criteria('updated_by').regex(filter.updatedBy, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.createdBy)) {
                query.addCriteria(new Criteria('created_by').regex(filter.createdBy, 'i'))
            }
        }

        return new PageImpl<>(mongoTemplate.find(query, GroupDTO.class), pageable, count())
    }

    @Override
    List<Group> find(Set<String> groupCodes) {
        Query query = Query.query(new Criteria('_id').in(groupCodes))
        return fromDomain(mongoTemplate.find(query, GroupDTO.class))
    }

    @Override
    Group create(final Group group) {
        if (exists(group.code)) {
            throw new ServiceException(AuthDBEvent.GroupAlreadyExists, group.code)
        }
        group.members = group.members ? group.members + adminLoginName : [adminLoginName]
        group.createDate = DateTime.now()
        group.createdBy = group.createdBy ?: 'unknown'
        silentSave group
        mapNewUsers group
        return group
    }

    @Override
    Group save(final Group group) {
        Group old = find group.code
        silentSave group
        eventBus.post(new GroupChanged(from: old, to: group))
        return group
    }

    @Override
    Group silentSave(final Group group) {
        group.updateDate = DateTime.now()
        group.updatedBy = group.updatedBy ?: 'unknown'
        mongoTemplate.save(toDomain(group))
        return group
    }

    @Override
    Group find(final String code) {
        return fromDomain(mongoTemplate.findOne(Query.query(Criteria.where('_id').is(code)), GroupDTO.class))
    }

    @Override
    boolean exists(final String code) {
        return mongoTemplate.findOne(Query.query(Criteria.where('_id').is(code)), GroupDTO.class)
    }

    @Override
    long count() {
        return mongoTemplate.count(null, GroupDTO.class)
    }

    @Override
    void delete(final String code) {
        Group group = find code
        if (!group) {
            throw new ServiceException(AuthDBEvent.GroupNotFound, code)
        }
        mongoTemplate.remove(Query.query(Criteria.where('_id').is(code)), GroupDTO.class)
        eventBus.post(new GroupRemoved(group: group))
    }

    @Override
    void delete(final Group group) {
        delete(group.code)
    }

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        if (mongoTemplate?.db?.mongo) {
            location = mongoTemplate.db.mongo.connectPoint

            long timeStart = new Date().time
            try {
                mongoTemplate.count(null, Group.class)
            } catch (final Exception e) {
                exceptionMessage = e.message
                exceptionDetails = ExceptionUtils.getStackTrace(e)
                statusType = StatusType.RED
            } finally {
                responseTime = new Date().time - timeStart
            }
        } else {
            statusType = StatusType.RED
        }
        return new ComponentStatus(
                name: 'Group DAO',
                location: location,
                status: statusType,
                componentType: ComponentStatus.ComponentType.DB,
                responseTime: responseTime,
                exceptionMessage: exceptionMessage,
                exceptionDetails: exceptionDetails
        )
    }
}
