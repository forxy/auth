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
import common.status.pojo.ComponentStatus;
import common.status.pojo.StatusType;
import auth.db.dao.IClientDAO;
import auth.api.v1.pojo.Client;

import java.util.Date;

/**
 * Mongo DB based data source for auths
 */
public class ClientDAO implements IClientDAO {

    private MongoTemplate mongoTemplate;

    @Override
    public Iterable<Client> findAll(final Sort sort) {
        return mongoTemplate.find(Query.query(new Criteria()).with(sort), Client.class);
    }

    @Override
    public Page<Client> findAll(final Pageable pageable) {
        return new PageImpl<>(
                mongoTemplate.find(Query.query(new Criteria()).with(pageable), Client.class),
                pageable, count()
        );
    }

    @Override
    public Page<Client> findAll(final Pageable pageable, final Client filter) {
        Query query = Query.query(new Criteria()).with(pageable);
        if (filter != null) {
            if (StringUtils.isNotEmpty(filter.getClientID())) {
                query.addCriteria(new Criteria("clientID").regex(filter.getClientID(), "i"));
            }
            if (StringUtils.isNotEmpty(filter.getName())) {
                query.addCriteria(new Criteria("name").regex(filter.getName(), "i"));
            }
            if (StringUtils.isNotEmpty(filter.getUpdatedBy())) {
                query.addCriteria(new Criteria("updatedBy").regex(filter.getUpdatedBy(), "i"));
            }
            if (StringUtils.isNotEmpty(filter.getCreatedBy())) {
                query.addCriteria(new Criteria("createdBy").regex(filter.getCreatedBy(), "i"));
            }
        }

        return new PageImpl<>(mongoTemplate.find(query, Client.class), pageable, count());
    }

    @Override
    public <S extends Client> S save(final S client) {
        mongoTemplate.save(client);
        return client;
    }

    @Override
    public <S extends Client> Iterable<S> save(final Iterable<S> clients) {
        throw null;
    }

    @Override
    public Client findOne(final String clientID) {
        return mongoTemplate.findOne(Query.query(Criteria.where("clientID").is(clientID)), Client.class);
    }

    @Override
    public boolean exists(final String clientID) {
        return mongoTemplate.findOne(Query.query(Criteria.where("clientID").is(clientID)), Client.class) != null;
    }

    @Override
    public Iterable<Client> findAll() {
        return mongoTemplate.findAll(Client.class);
    }

    @Override
    public Iterable<Client> findAll(final Iterable<String> clientIDs) {
        return mongoTemplate.find(Query.query(Criteria.where("clientID").in(clientIDs)), Client.class);
    }

    @Override
    public long count() {
        return mongoTemplate.count(null, Client.class);
    }

    @Override
    public void delete(final String clientID) {
        mongoTemplate.remove(Query.query(Criteria.where("clientID").is(clientID)), Client.class);
    }

    @Override
    public void delete(final Client client) {
        mongoTemplate.remove(client);
    }

    @Override
    public void delete(final Iterable<? extends Client> clients) {
        for (Client client : clients) {
            mongoTemplate.remove(client);
        }
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(null, Client.class);
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
                mongoTemplate.count(null, Client.class);
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
        return new ComponentStatus("Client DAO", location, statusType, null, ComponentStatus.ComponentType.DB,
                responseTime, null, exceptionMessage, exceptionDetails);
    }
}
