package auth.db.dao.mongo

import auth.api.v1.pojo.Client
import auth.db.dao.IClientDAO
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
class ClientDAO implements IClientDAO {

    MongoTemplate mongoTemplate

    @Override
    Iterable<Client> findAll(final Sort sort) {
        return mongoTemplate.find(Query.query(new Criteria()).with(sort), Client.class)
    }

    @Override
    Page<Client> findAll(final Pageable pageable) {
        return new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), Client.class),
                pageable, count()
        )
    }

    @Override
    Client save(final Client client) {
        mongoTemplate.save(client)
        return client
    }

    @Override
    Page<Client> findAll(final Pageable pageable, final Client filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.getClientID())) {
                query.addCriteria(new Criteria('clientID').regex(filter.getClientID(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getName())) {
                query.addCriteria(new Criteria('name').regex(filter.getName(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getUpdatedBy())) {
                query.addCriteria(new Criteria('updatedBy').regex(filter.getUpdatedBy(), 'i'))
            }
            if (StringUtils.isNotEmpty(filter.getCreatedBy())) {
                query.addCriteria(new Criteria('createdBy').regex(filter.getCreatedBy(), 'i'))
            }
        }

        return new PageImpl<>(mongoTemplate.find(query, Client.class), pageable, count())
    }

    @Override
    Client findOne(final String clientID) {
        return mongoTemplate.findOne(Query.query(Criteria.where('clientID').is(clientID)), Client.class)
    }

    @Override
    boolean exists(final String clientID) {
        return mongoTemplate.findOne(Query.query(Criteria.where('clientID').is(clientID)), Client.class) != null
    }

    @Override
    Iterable<Client> findAll() {
        return mongoTemplate.findAll(Client.class)
    }

    @Override
    long count() {
        return mongoTemplate.count(null, Client.class)
    }

    @Override
    void delete(final String clientID) {
        mongoTemplate.remove(Query.query(Criteria.where('clientID').is(clientID)), Client.class)
    }

    @Override
    void delete(final Client client) {
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
                mongoTemplate.count(null, Client.class)
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
        return new ComponentStatus('Client DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
