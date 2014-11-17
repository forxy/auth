package auth.api.v1.pojo

import common.pojo.SimpleJacksonDateDeserializer
import common.pojo.SimpleJacksonDateSerializer
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.map.annotate.JsonDeserialize
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@EqualsAndHashCode
@Document(collection = 'token')
class Token implements Serializable {

    @Id
    String tokenKey

    @Indexed
    String clientID

    String type

    String refreshToken

    List<String> scopes

    UserSubject subject

    Long expiresIn
    
    @Indexed
    Date issuedAt = new Date()

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    Date getIssuedAt() {
        return issuedAt
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    void setIssuedAt(final Date issuedAt) {
        this.issuedAt = issuedAt
    }
}
