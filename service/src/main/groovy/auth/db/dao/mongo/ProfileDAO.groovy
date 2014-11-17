package auth.db.dao.mongo

import auth.api.v1.pojo.Profile
import auth.db.dao.IProfileDAO
import common.status.pojo.ComponentStatus
import common.status.pojo.StatusType
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * Mongo DB based data source for users
 */
class ProfileDAO implements IProfileDAO {

    MongoTemplate mongoTemplate

    @Override
    Profile save(final Profile profile) {
        mongoTemplate.save(profile)
        return profile
    }

    @Override
    Profile findOne(final String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where('email').is(email)), Profile.class)
    }

    @Override
    boolean exists(final String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where('email').is(email)), Profile.class) != null
    }

    @Override
    void delete(final String email) {
        mongoTemplate.remove(Query.query(Criteria.where('email').is(email)), Profile.class)
    }

    @Override
    void delete(Profile profile) {
        mongoTemplate.remove(Query.query(Criteria.where('email').is(profile.getEmail())), Profile.class)
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
                mongoTemplate.count(null, Profile.class)
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
        return new ComponentStatus('Profile DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
