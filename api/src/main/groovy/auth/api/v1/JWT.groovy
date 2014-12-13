package auth.api.v1

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical

/**
 * JSON Web Token body
 */
@Canonical
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
