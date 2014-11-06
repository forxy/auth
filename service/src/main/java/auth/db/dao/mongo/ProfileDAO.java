package auth.db.dao.mongo;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import auth.db.dao.IProfileDAO;
import auth.api.v1.pojo.Profile;
import common.status.pojo.ComponentStatus;
import common.status.pojo.StatusType;

import java.util.Date;

/**
 * Mongo DB based data source for users
 */
public class ProfileDAO implements IProfileDAO {

    MongoTemplate mongoTemplate;

    @Override
    public <S extends Profile> S save(final S profile) {
        mongoTemplate.save(profile);
        return profile;
    }

    @Override
    public <S extends Profile> Iterable<S> save(final Iterable<S> profiles) {
        throw null;
    }

    @Override
    public Profile findOne(final String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), Profile.class);
    }

    @Override
    public boolean exists(final String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), Profile.class) != null;
    }

    @Override
    public Iterable<Profile> findAll() {
        return null;
    }

    @Override
    public Iterable<Profile> findAll(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return mongoTemplate.count(null, Profile.class);
    }

    @Override
    public void delete(final String email) {
        mongoTemplate.remove(Query.query(Criteria.where("email").is(email)), Profile.class);
    }

    @Override
    public void delete(Profile profile) {
        mongoTemplate.remove(Query.query(Criteria.where("email").is(profile.getEmail())), Profile.class);
    }

    @Override
    public void delete(Iterable<? extends Profile> profiles) {
        throw null;
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(null, Profile.class);
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
                mongoTemplate.count(null, Profile.class);
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
        return new ComponentStatus("Profile DAO", location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails);
    }
}
