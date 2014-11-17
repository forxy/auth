package auth.api.v1.pojo

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON Web Token body
 */
@ToString
@EqualsAndHashCode
class JWT {

    @JsonProperty('iss')
    String issuer;

    @JsonProperty('sub')
    String subject;

    @JsonProperty('aud')
    String audience;

    @JsonProperty('exp')
    Long expirationTime;

    @JsonProperty('nbf')
    Long notBefore;

    @JsonProperty('iat')
    Long issuedAt;

    @JsonProperty('jti')
    String jwtID;
}
