package auth.test.dvt

import auth.api.v1.pojo.Client
import auth.client.v1.IAuthServiceClient
import auth.test.BaseAuthServiceTest
import common.exceptions.ClientException
import common.pojo.EntityPage
import common.pojo.StatusEntity
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Test success path for AuthService client
 */
class AuthServiceClientSuccessPathTest extends BaseAuthServiceTest {

    @Autowired
    IAuthServiceClient authServiceClient

    @Test
    void testGetAllAuths() {
        Assert.assertNotNull(authServiceClient)
        String transactionGUID = UUID.randomUUID().toString()
        EntityPage<Client> authPage = authServiceClient.getClients(transactionGUID, 1)
        Assert.assertNotNull(authPage)
    }

    @Test
    void createUpdateDeleteAuth() {
        // prepare test data
        String transactionGUID = UUID.randomUUID().toString()
        Client expectedClient = new Client()
        expectedClient.setName('Test')
        expectedClient.setSecret('secret')
        expectedClient.setDescription('Description')
        StatusEntity status
        try {
            // creating auth
            status = authServiceClient.createClient(transactionGUID, expectedClient)
            Assert.assertEquals('200', status.getCode())
            Client actualClient = authServiceClient.getClient(transactionGUID, expectedClient.getName())
            Assert.assertEquals(expectedClient.getName(), actualClient.getName())

            // updating auth
            expectedClient.setName('Test2')
            status = authServiceClient.updateClient(transactionGUID, expectedClient)
            Assert.assertEquals('200', status.getCode())
            actualClient = authServiceClient.getClient(transactionGUID, expectedClient.getName())
            Assert.assertEquals(expectedClient.getName(), actualClient.getName())
        } catch (Throwable th) {
            th.printStackTrace()
        } finally {
            // deleting auth
            status = authServiceClient.deleteClient(transactionGUID, expectedClient.getName())
            Assert.assertEquals('200', status.getCode())
            try {
                authServiceClient.getClient(transactionGUID, expectedClient.getName())
                Assert.fail()
            } catch (ClientException e) {
                Assert.assertNotNull(e.getErrorEntity())
                Assert.assertEquals('404', e.getErrorEntity().getCode())
            }
        }
    }
}
