package auth.client.v1;

import auth.api.v1.pojo.Client;
import common.pojo.EntityPage;
import common.pojo.StatusEntity;

import java.util.List;

public interface IAuthServiceClient {

    List getClients(final String transactionGUID);

    EntityPage<Client> getClients(final String transactionGUID, final Integer page);

    EntityPage<Client> getClients(final String transactionGUID, final Integer page, final Integer size);

    Client getClient(final String transactionGUID, final String email);

    StatusEntity createClient(final String transactionGUID, final Client client);

    StatusEntity updateClient(final String transactionGUID, final Client client);

    StatusEntity deleteClient(final String transactionGUID, final String clientID);
}
