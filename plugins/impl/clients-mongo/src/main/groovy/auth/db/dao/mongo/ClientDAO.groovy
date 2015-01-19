package auth.db.dao.mongo

import auth.api.v1.Client
import auth.db.dao.BaseClientDAO
import auth.db.dao.mongo.domain.ClientDTO
import auth.db.event.ClientChanged
import auth.db.event.ClientRemoved
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

import static auth.db.dao.mongo.domain.ClientDTO.fromDomain
import static auth.db.dao.mongo.domain.ClientDTO.toDomain

/**
 * Mongo DB based data source for clients
 */
class ClientDAO extends BaseClientDAO {

    MongoTemplate mongoTemplate
    PasswordEncoder passwordEncoder

    @Override
    List<Client> findAll() {
        fromDomain(mongoTemplate.findAll(ClientDTO.class))
    }

    @Override
    List<Client> find(final Sort sort) {
        fromDomain(mongoTemplate.find(Query.query(new Criteria()).with(sort), ClientDTO.class))
    }

    @Override
    Page<Client> find(final Pageable pageable) {
        return new PageImpl<Client>(
                fromDomain(mongoTemplate.find(Query.query(new Criteria()).with(pageable), ClientDTO.class)),
                pageable, count()
        )
    }

    @Override
    Client authenticate(final String clientID, final String secret) {
        Client client = find clientID
        if (client && passwordEncoder.matches(secret, client.password)) {
            return client
        } else {
            return null
        }
    }

    @Override
    Page<Client> find(final Pageable pageable, final Client filter) {
        Query query = Query.query(new Criteria()).with(pageable)
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.clientID)) {
                query.addCriteria(new Criteria('_id').regex(filter.clientID, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.email)) {
                query.addCriteria(new Criteria('email').regex(filter.email, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.name)) {
                query.addCriteria(new Criteria('name').regex(filter.name, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.description)) {
                query.addCriteria(new Criteria('description').regex(filter.description, 'i'))
            }
            if (StringUtils.isNotEmpty(filter.updatedBy)) {
                query.addCriteria(new Criteria('updated_by').regex(filter.updatedBy, 'i'))
            }
        }

        return new PageImpl<Client>(fromDomain(mongoTemplate.find(query, ClientDTO.class)), pageable, count())
    }

    @Override
    List<Client> find(Set<String> ids) {
        Query query = Query.query(new Criteria('_id').in(ids))
        return fromDomain(mongoTemplate.find(query, ClientDTO.class))
    }

    @Override
    Client find(final String id) {
        fromDomain(mongoTemplate.findOne(
                Query.query(
                        new Criteria().orOperator(
                                Criteria.where('_id').is(id),
                                Criteria.where('email').is(id)
                        )
                ), ClientDTO.class))
    }

    @Override
    Client create(final Client client) {
        if (!client.clientID) {
            client.clientID = UUID.randomUUID() as String
        } else if (exists(client.clientID)) {
            throw new ServiceException(AuthDBEvent.ClientAlreadyExists, client.clientID)
        }
        client.createDate = DateTime.now()
        client.createdBy = client.createdBy ?: 'unknown'
        return silentSave(client)
    }

    @Override
    Client save(final Client client) {
        Client old = find client.clientID
        silentSave client
        eventBus.post(new ClientChanged(from: old, to: client))
        return client
    }

    @Override
    Client silentSave(final Client client) {
        client.password = passwordEncoder.encode(client.password)
        client.updateDate = DateTime.now()
        client.updatedBy = client.updatedBy ?: 'unknown'
        mongoTemplate.save toDomain(client)
        return client
    }

    @Override
    boolean exists(final String id) {
        mongoTemplate.findOne(Query.query(Criteria.where('_id').is(id)), ClientDTO.class)
    }

    @Override
    long count() {
        mongoTemplate.count(null, ClientDTO.class)
    }

    @Override
    void delete(final String clientID) {
        Client client = find clientID
        if (!client) {
            throw new ServiceException(AuthDBEvent.ClientNotFound, clientID)
        }
        mongoTemplate.remove(Query.query(Criteria.where('_id').is(client.clientID)), ClientDTO.class)
        eventBus.post(new ClientRemoved(client: client))
    }

    @Override
    void delete(final Client client) {
        delete client.clientID
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
                mongoTemplate.count(null, ClientDTO.class)
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
        return new ComponentStatus('Clients DAO', location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails)
    }
}
