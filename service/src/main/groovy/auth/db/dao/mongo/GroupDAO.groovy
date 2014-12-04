package auth.db.dao.mongo

import auth.api.v1.Group
import auth.db.dao.IGroupDAO
import common.status.pojo.ComponentStatus
import common.status.pojo.StatusType
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * Mongo DB based data source for auths
 */
class GroupDAO implements IGroupDAO {

    MongoTemplate mongoTemplate

    @Override
    Iterable<Group> findAll(final Sort sort) {
        return mongoTemplate.find(Query.query(new Criteria()).with(sort), Group.class)
    }

    @Override
    Page<Group> findAll(final Pageable pageable) {
        return new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), Group.class),
                pageable, count()
        )
    }

    @Override
    Page<Group> findAll(final Pageable pageable, final Group filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.getCode())) {
                query.addCriteria(new Criteria('_id').regex(filter.getCode(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getName())) {
                query.addCriteria(new Criteria('name').regex(filter.getName(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getUpdatedBy())) {
                query.addCriteria(new Criteria('updated_by').regex(filter.getUpdatedBy(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getCreatedBy())) {
                query.addCriteria(new Criteria('created_by').regex(filter.getCreatedBy(), 'i'))
            }
        }

        return new PageImpl<>(mongoTemplate.find(query, Group.class), pageable, count())
    }

    @Override
    List<Group> findAll(List<String> groupCodes) {
        Query query = Query.query(new Criteria('_id').in(groupCodes))
        return mongoTemplate.find(query, Group.class)
    }

    @Override
    Group save(final Group group) {
        mongoTemplate.save(group)
        return group
    }

    @Override
    Group findOne(final String code) {
        return mongoTemplate.findOne(Query.query(Criteria.where('code').is(code)), Group.class)
    }

    @Override
    boolean exists(final String code) {
        return mongoTemplate.findOne(Query.query(Criteria.where('code').is(code)), Group.class) != null
    }

    @Override
    Iterable<Group> findAll() {
        return mongoTemplate.findAll(Group.class)
    }

    @Override
    long count() {
        return mongoTemplate.count(null, Group.class)
    }

    @Override
    void delete(final String code) {
        mongoTemplate.remove(Query.query(Criteria.where('code').is(code)), Group.class)
    }

    @Override
    void delete(final Group client) {
        mongoTemplate.remove(client)
    }

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        if (mongoTemplate != null && mongoTemplate.getDb() != null && mongoTemplate.getDb().getMongo() != null) {
            location = mongoTemplate.getDb().getMongo().getConnectPoint()

            long timeStart = new Date().getTime()
            try {
                mongoTemplate.count(null, Group.class)
            } catch (final Exception e) {
                exceptionMessage = e.getMessage()
                exceptionDetails = ExceptionUtils.getStackTrace(e)
                statusType = StatusType.RED
            } finally {
                responseTime = new Date().getTime() - timeStart
            }


        } else {
            statusType = StatusType.RED
        }
        return new ComponentStatus('Group DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
