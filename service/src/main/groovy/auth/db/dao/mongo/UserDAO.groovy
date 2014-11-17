package auth.db.dao.mongo

import auth.api.v1.pojo.User
import auth.db.dao.IUserDAO
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
 * Mongo DB based data source for users
 */
class UserDAO implements IUserDAO {

    MongoTemplate mongoTemplate

    @Override
    Iterable<User> findAll(final Sort sort) {
        mongoTemplate.find(Query.query(new Criteria()).with(sort), User.class)
    }

    @Override
    Page<User> findAll(final Pageable pageable) {
        return new PageImpl<User>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), User.class),
                pageable, count()
        )
    }

    @Override
    Page<User> findAll(final Pageable pageable, final User filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.getEmail())) {
                query.addCriteria(new Criteria('email').regex(filter.getEmail(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getLogin())) {
                query.addCriteria(new Criteria('login').regex(filter.getLogin(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getFirstName())) {
                query.addCriteria(new Criteria('first_name').regex(filter.getFirstName(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getLastName())) {
                query.addCriteria(new Criteria('last_name').regex(filter.getLastName(), 'i'))
            }
            if (filter.getGender() != null) {
                query.addCriteria(new Criteria('gender').is(filter.getGender()))
            }
        }

        return new PageImpl<User>(mongoTemplate.find(query, User.class), pageable, count())
    }

    @Override
    User save(final User user) {
        mongoTemplate.save(user)
        return user
    }

    @Override
    User findOne(final String email) {
        mongoTemplate.findOne(Query.query(Criteria.where('email').is(email)), User.class)
    }

    @Override
    boolean exists(final String email) {
        mongoTemplate.findOne(Query.query(Criteria.where('email').is(email)), User.class) != null
    }

    @Override
    Iterable<User> findAll() {
        mongoTemplate.findAll(User.class)
    }

    @Override
    long count() {
        mongoTemplate.count(null, User.class)
    }

    @Override
    void delete(final String email) {
        mongoTemplate.remove(Query.query(Criteria.where('email').is(email)), User.class)
    }

    @Override
    void delete(final User user) {
        mongoTemplate.remove(user)
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
                mongoTemplate.count(null, User.class)
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
        return new ComponentStatus('User DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
