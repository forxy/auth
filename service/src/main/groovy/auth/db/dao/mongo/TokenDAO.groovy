package auth.db.dao.mongo

import auth.api.v1.Token
import auth.db.dao.ITokenDAO
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

/**
 * Mongo DB based data source for tokens
 */
class TokenDAO implements ITokenDAO {

    MongoTemplate mongoTemplate

    @Override
    Iterable<Token> findAll(final Sort sort) {
        return mongoTemplate.find(Query.query(new Criteria()).with(sort), Token.class)
    }

    @Override
    Page<Token> findAll(final Pageable pageable) {
        return new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), Token.class),
                pageable, count()
        )
    }

    @Override
    Page<Token> findAll(final Pageable pageable, final Token filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.tokenKey)) {
                query.addCriteria(new Criteria('tokenKey').regex(filter.tokenKey))
            }
            if (StringUtils.isNotEmpty(filter.clientID)) {
                query.addCriteria(new Criteria('clientID').regex(filter.clientID))
            }
            if (StringUtils.isNotEmpty(filter.type)) {
                query.addCriteria(new Criteria('type').regex(filter.type))
            }
        }

        return new PageImpl<>(mongoTemplate.find(query, Token.class), pageable, count())
    }

    @Override
    Token save(final Token token) {
        mongoTemplate.save(token)
        return token
    }

    @Override
    Token findOne(final String tokenKey) {
        return mongoTemplate.findOne(Query.query(Criteria.where('tokenKey').is(tokenKey)), Token.class)
    }

    @Override
    boolean exists(final String tokenKey) {
        return mongoTemplate.findOne(Query.query(Criteria.where('tokenKey').is(tokenKey)), Token.class) != null
    }

    @Override
    Iterable<Token> findAll() {
        return mongoTemplate.findAll(Token.class)
    }

    @Override
    long count() {
        return mongoTemplate.count(null, Token.class)
    }

    @Override
    void delete(final String tokenKey) {
        mongoTemplate.remove(Query.query(Criteria.where('tokenKey').is(tokenKey)), Token.class)
    }

    @Override
    void delete(final Token token) {
        mongoTemplate.remove(token)
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
                mongoTemplate.count(null, Token.class)
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
        return new ComponentStatus('Token DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
