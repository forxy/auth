package auth.db.dao.mongo

import auth.api.v1.User
import auth.db.dao.IUserDAO
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Mongo DB based data source for users
 */
class UserDAO implements IUserDAO {

    MongoTemplate mongoTemplate

    PasswordEncoder passwordEncoder

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
    User authenticate(final String login, final String password) {
        User user = findOne(login)
        if (user && passwordEncoder.matches(password, user.password)) {
            return user
        } else {
            return null
        }
    }

    @Override
    Page<User> findAll(final Pageable pageable, final User filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.email)) {
                query.addCriteria(new Criteria('email').regex(filter.email, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.login)) {
                query.addCriteria(new Criteria('login').regex(filter.login, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.firstName)) {
                query.addCriteria(new Criteria('first_name').regex(filter.firstName, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.lastName)) {
                query.addCriteria(new Criteria('last_name').regex(filter.lastName, 'i'))
            }
            if (filter.gender != null) {
                query.addCriteria(new Criteria('gender').is(filter.gender))
            }
        }

        return new PageImpl<User>(mongoTemplate.find(query, User.class), pageable, count())
    }

    @Override
    User save(final User user) {
        user.password = passwordEncoder.encode(user.password)
        mongoTemplate.save(user)
        return user
    }

    @Override
    User findOne(final String login) {
        mongoTemplate.findOne(
                Query.query(
                        new Criteria().orOperator(
                                Criteria.where('email').is(login),
                                Criteria.where('login').is(login)
                        )
                ), User.class)
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
        if (mongoTemplate != null && mongoTemplate.db != null && mongoTemplate.db.mongo != null) {
            location = mongoTemplate.db.mongo.connectPoint

            long timeStart = new Date().time
            try {
                mongoTemplate.count(null, User.class)
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
        return new ComponentStatus('User DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
