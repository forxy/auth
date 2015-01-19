package auth.db.dao.mongo.domain

import auth.api.v1.Client
import groovy.transform.Canonical
import org.codehaus.groovy.runtime.InvokerHelper
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = 'client')
class ClientDTO {
    @Id
    String clientID
    @Indexed(unique = true)
    String email
    String password
    @Indexed(unique = true)
    String name
    String description
    String webUri

    DateTime updateDate
    String updatedBy
    DateTime createDate
    String createdBy

    List<String> redirectUris = new ArrayList<>()
    List<String> scopes = new ArrayList<>()
    List<String> audiences = new ArrayList<>()

    public static ClientDTO toDomain(Client client) {
        ClientDTO domain = new ClientDTO()
        InvokerHelper.setProperties(domain, client?.properties)
        domain.scopes = domain.scopes ?: []
        return domain
    }

    public static Client fromDomain(ClientDTO domain) {
        Client client = new Client()
        InvokerHelper.setProperties(client, domain?.properties)
        client.scopes = client.scopes ?: []
        return client
    }

    public static List<Client> fromDomain(List<ClientDTO> domains) {
        domains?.collect {
            fromDomain(it)
        }
    }
}
