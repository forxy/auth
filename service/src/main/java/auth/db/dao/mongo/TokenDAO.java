package auth.db.dao.mongo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import auth.db.dao.ITokenDAO;
import auth.api.v1.pojo.Token;
import common.status.pojo.ComponentStatus;
import common.status.pojo.StatusType;

import java.util.Date;

/**
 * Mongo DB based data source for tokens
 */
public class TokenDAO implements ITokenDAO {

    MongoTemplate mongoTemplate;

    @Override
    public Iterable<Token> findAll(final Sort sort) {
        return mongoTemplate.find(Query.query(new Criteria()).with(sort), Token.class);
    }

    @Override
    public Page<Token> findAll(final Pageable pageable) {
        return new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), Token.class),
                pageable, count()
        );
    }

    @Override
    public Page<Token> findAll(final Pageable pageable, final Token filter) {
        Query query = Query.query(new Criteria()).with(pageable);
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.getTokenKey())) {
                query.addCriteria(new Criteria("tokenKey").regex(filter.getTokenKey()));
            }
            if (StringUtils.isNotEmpty(filter.getClientID())) {
                query.addCriteria(new Criteria("clientID").regex(filter.getClientID()));
            }
            if (StringUtils.isNotEmpty(filter.getType())) {
                query.addCriteria(new Criteria("type").regex(filter.getType()));
            }
        }

        return new PageImpl<>(mongoTemplate.find(query, Token.class), pageable, count());
    }

    @Override
    public <S extends Token> S save(final S token) {
        mongoTemplate.save(token);
        return token;
    }

    @Override
    public <S extends Token> Iterable<S> save(final Iterable<S> tokens) {
        throw null;
    }

    @Override
    public Token findOne(final String tokenKey) {
        return mongoTemplate.findOne(Query.query(Criteria.where("tokenKey").is(tokenKey)), Token.class);
    }

    @Override
    public boolean exists(final String tokenKey) {
        return mongoTemplate.findOne(Query.query(Criteria.where("tokenKey").is(tokenKey)), Token.class) != null;
    }

    @Override
    public Iterable<Token> findAll() {
        return mongoTemplate.findAll(Token.class);
    }

    @Override
    public Iterable<Token> findAll(final Iterable<String> tokenKeys) {
        return mongoTemplate.find(Query.query(Criteria.where("tokenKey").in(tokenKeys)), Token.class);
    }

    @Override
    public long count() {
        return mongoTemplate.count(null, Token.class);
    }

    @Override
    public void delete(final String tokenKey) {
        mongoTemplate.remove(Query.query(Criteria.where("tokenKey").is(tokenKey)), Token.class);
    }

    @Override
    public void delete(final Token token) {
        mongoTemplate.remove(token);
    }

    @Override
    public void delete(final Iterable<? extends Token> tokens) {
        for (Token token : tokens) {
            mongoTemplate.remove(token);
        }
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(null, Token.class);
    }

    public void setMongoTemplate(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ComponentStatus getStatus() {
        String location = null;
        StatusType statusType = StatusType.GREEN;
        long responseTime = Long.MAX_VALUE;
        String exceptionMessage = null;
        String exceptionDetails = null;
        if (mongoTemplate != null && mongoTemplate.getDb() != null && mongoTemplate.getDb().getMongo() != null) {
            location = mongoTemplate.getDb().getMongo().getConnectPoint();

            long timeStart = new Date().getTime();
            try {
                mongoTemplate.count(null, Token.class);
            } catch (final Exception e) {
                exceptionMessage = e.getMessage();
                exceptionDetails = ExceptionUtils.getStackTrace(e);
                statusType = StatusType.RED;
            } finally {
                responseTime = new Date().getTime() - timeStart;
            }


        } else {
            statusType = StatusType.RED;
        }
        return new ComponentStatus("Token DAO", location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails);
    }
}
