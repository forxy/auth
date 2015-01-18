package auth.db.dao.mongo

import auth.api.v1.User
import auth.db.dao.BaseUserDAO
import auth.db.dao.mongo.domain.ProfileDTO
import auth.db.dao.mongo.domain.UserDTO
import auth.db.event.UserChanged
import auth.db.event.UserRemoved
import auth.db.exceptions.AuthDBEvent
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
import org.springframework.security.crypto.password.PasswordEncoder

import static auth.db.dao.mongo.domain.ProfileDTO.fromDomain
import static auth.db.dao.mongo.domain.ProfileDTO.toDomain
import static auth.db.dao.mongo.domain.UserDTO.fromDomain

/**
 * Mongo DB based data source for users
 */
class UserDAO extends BaseUserDAO {

    MongoTemplate mongoTemplate
    PasswordEncoder passwordEncoder

    @Override
    List<User> find(final Sort sort) {
        fromDomain(mongoTemplate.find(Query.query(new Criteria()).with(sort), UserDTO.class))
    }

    @Override
    Page<User> find(final Pageable pageable) {
        return new PageImpl<User>(
                fromDomain(mongoTemplate.find(Query.query(new Criteria()).with(pageable), UserDTO.class)),
                pageable, count()
        )
    }

    @Override
    User authenticate(final String login, final String password) {
        User user = find login
        if (user && passwordEncoder.matches(password, user.password)) {
            return user
        } else {
            return null
        }
    }

    @Override
    Page<User> find(final Pageable pageable, final User filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.email)) {
                query.addCriteria(new Criteria('email').regex(filter.email, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.login)) {
                query.addCriteria(new Criteria('_id').regex(filter.login, 'i'))
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

        return new PageImpl<User>(fromDomain(mongoTemplate.find(query, UserDTO.class)), pageable, count())
    }

    @Override
    List<User> find(Set<String> ids) {
        Query query = Query.query(new Criteria('_id').in(ids))
        return fromDomain(mongoTemplate.find(query, UserDTO.class))
    }

    @Override
    User getProfile(final String login) {
        return fromDomain(mongoTemplate.findOne(Query.query(Criteria.where('_id').is(login)), ProfileDTO.class))
    }

    @Override
    User create(final User user) {
        if (exists(user.login)) {
            throw new ServiceException(AuthDBEvent.UserAlreadyExists, user.login)
        }
        user.createDate = DateTime.now()
        user.createdBy = user.createdBy ?: 'unknown'
        silentSave user
        mapNewGroups user
        return user
    }

    @Override
    User save(final User user) {
        User old = find user.login
        silentSave user
        eventBus.post(new UserChanged(from: old, to: user))
        return user
    }

    @Override
    User silentSave(final User user) {
        user.password = passwordEncoder.encode(user.password)
        user.updateDate = DateTime.now()
        user.updatedBy = user.updatedBy ?: 'unknown'
        mongoTemplate.save(toDomain(user))
        return user
    }

    @Override
    User updateProfile(final User profile) {
        profile.password = passwordEncoder.encode(profile.password)
        profile.updateDate = DateTime.now()
        profile.updatedBy = profile.updatedBy ?: 'unknown'
        User old = getProfile profile.login
        eventBus.post(new UserChanged(from: old, to: profile))
        mongoTemplate.save(toDomain(profile))
        return profile
    }

    @Override
    User find(final String id) {
        fromDomain(mongoTemplate.findOne(
                Query.query(
                        new Criteria().orOperator(
                                Criteria.where('_id').is(id),
                                Criteria.where('email').is(id)
                        )
                ), UserDTO.class))
    }

    @Override
    boolean exists(final String id) {
        mongoTemplate.findOne(Query.query(Criteria.where('_id').is(id)), UserDTO.class)
    }

    @Override
    List<User> findAll() {
        fromDomain(mongoTemplate.findAll(UserDTO.class))
    }

    @Override
    long count() {
        mongoTemplate.count(null, UserDTO.class)
    }

    @Override
    void delete(final String login) {
        User user = find login
        if (!user) {
            throw new ServiceException(AuthDBEvent.UserNotFound, login)
        }
        mongoTemplate.remove(Query.query(Criteria.where('_id').is(user.login)), UserDTO.class)
        eventBus.post(new UserRemoved(user: user))
    }

    @Override
    void delete(final User user) {
        delete user.login
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
                mongoTemplate.count(null, UserDTO.class)
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
