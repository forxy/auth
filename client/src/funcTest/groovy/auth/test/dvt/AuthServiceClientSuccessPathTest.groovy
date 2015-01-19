package auth.test.dvt

import auth.api.v1.Client
import auth.test.BaseAuthServiceTest
import common.api.EntityPage
import common.api.StatusEntity
import common.exceptions.ClientException
import org.junit.Test

import static org.junit.Assert.*

/**
 * Test success path for AuthService client
 */
class AuthServiceClientSuccessPathTest extends BaseAuthServiceTest {

    @Test
    void testGetAllAuths() {
        assertNotNull authServiceClient
        EntityPage<Client> authPage = authServiceClient.getClients(transactionGUID, 1)
        assertNotNull authPage
    }

    @Test
    void createUpdateDeleteAuth() {
        // prepare test data
        Client expectedClient = new Client(name: 'Test', password: 'password', description: 'Description')
        StatusEntity status
        try {
            // creating auth
            status = authServiceClient.createClient(transactionGUID, expectedClient)
            assertEquals(200, status.code)
            Client actualClient = authServiceClient.getClient(transactionGUID, expectedClient.name)
            assertEquals(expectedClient.name, actualClient.name)

            // updating auth
            expectedClient.name = 'Test2'
            status = authServiceClient.updateClient(transactionGUID, expectedClient)
            assertEquals(200, status.code)
            actualClient = authServiceClient.getClient(transactionGUID, expectedClient.name)
            assertEquals(expectedClient.name, actualClient.name)
        } catch (Throwable th) {
            th.printStackTrace()
        } finally {
            // deleting auth
            status = authServiceClient.deleteClient(transactionGUID, expectedClient.name)
            assertEquals(200, status.code)
            try {
                authServiceClient.getClient(transactionGUID, expectedClient.name)
                fail('Client should not exist after removal')
            } catch (ClientException e) {
                assertNotNull e.errorEntity
                assertEquals(404, e.errorEntity.code)
            }
        }
    }
}
