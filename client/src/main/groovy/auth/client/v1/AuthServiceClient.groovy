package auth.client.v1

import auth.api.v1.Client
import common.exceptions.ClientException
import common.exceptions.HttpEvent
import common.pojo.EntityPage
import common.pojo.StatusEntity
import common.rest.client.RestServiceClientSupport
import common.rest.client.transport.HttpClientSSLKeyStore
import common.rest.client.transport.ITransport
import common.rest.client.transport.support.ObjectMapperProvider
import org.apache.commons.io.IOUtils

/**
 * Client service client implementation
 */
class AuthServiceClient extends RestServiceClientSupport implements IAuthServiceClient {
    //private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd\'T\'HH:mm:ssZ')
    private static final HttpClientSSLKeyStore TRUST_STORE

    static {
        try {
            final InputStream trustStoreStream =
                    AuthServiceClient.class.getResourceAsStream('/cert/oauthTrustStore.jks')
            final byte[] trustStoreBytes = IOUtils.toByteArray(trustStoreStream)
            TRUST_STORE = new HttpClientSSLKeyStore(new ByteArrayInputStream(trustStoreBytes), '5ecret0AUTHPa55word')
        } catch (Exception e) {
            TRUST_STORE = null
            throw new ClientException(new StatusEntity('400', e), e, HttpEvent.InvalidClientInput)
        }
    }

    AuthServiceClient(final String endpoint, final String clientID, final ITransport transport) {
        this.endpoint = endpoint
        this.clientID = clientID
        this.transport = transport

        mapper = ObjectMapperProvider.defaultMapper
        /*ObjectMapperProvider.getMapper(
                ObjectMapperProvider.Config.newInstance().
                        withDateFormat(SIMPLE_DATE_FORMAT).
                        writeEmptyArrays(true).writeNullMapValues(true)
        )*/
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    List<Client> getClients(String transactionGUID) {
        final String confUrl = "$endpoint/clients"

        final ITransport.Response<List, StatusEntity> response =
                transport.performGet(confUrl, buildHeaders(transactionGUID),
                        createResponseHandler(List.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    EntityPage<Client> getClients(final String transactionGUID, final Integer page) {
        final String confUrl = "$endpoint/clients?page=$page"

        final ITransport.Response<EntityPage, StatusEntity> response =
                transport.performGet(confUrl, buildHeaders(transactionGUID),
                        createResponseHandler(EntityPage.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    EntityPage<Client> getClients(final String transactionGUID, Integer page, Integer size) {
        final String confUrl = "$endpoint/clients?page=$page&size=$size"

        final ITransport.Response<EntityPage, StatusEntity> response =
                transport.performGet(confUrl, buildHeaders(transactionGUID),
                        createResponseHandler(EntityPage.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    Client getClient(final String transactionGUID, String email) {
        final String confUrl = "$endpoint/clients/$email"

        final ITransport.Response<Client, StatusEntity> response =
                transport.performGet(confUrl, buildHeaders(transactionGUID),
                        createResponseHandler(Client.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    StatusEntity createClient(final String transactionGUID, final Client client) {
        final String confUrl = "$endpoint/clients"

        final ITransport.Response<StatusEntity, StatusEntity> response =
                transport.performPost(confUrl, buildHeaders(transactionGUID),
                        marshal(client), createResponseHandler(StatusEntity.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    StatusEntity updateClient(final String transactionGUID, final Client client) {
        final String confUrl = "$endpoint/clients"

        final ITransport.Response<StatusEntity, StatusEntity> response =
                transport.performPut(confUrl, buildHeaders(transactionGUID),
                        marshal(client), createResponseHandler(StatusEntity.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    StatusEntity deleteClient(String transactionGUID, String clientID) {
        final String confUrl = "$endpoint/clients/$clientID"

        final ITransport.Response<StatusEntity, StatusEntity> response =
                transport.performDelete(confUrl, buildHeaders(transactionGUID),
                        createResponseHandler(StatusEntity.class))
        return checkForError(response)
    }
}
