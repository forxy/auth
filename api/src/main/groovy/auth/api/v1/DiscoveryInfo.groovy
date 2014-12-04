package auth.api.v1

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * OpenID configuration
 */
@ToString
@EqualsAndHashCode
class DiscoveryInfo implements Serializable {

    String issuer

    String authorizationEndpoint

    String tokenEndpoint

    String userinfoEndpoint

    String revocationEndpoint

    String jwksUri

    List<String> responseTypesSupported

    List<String> subjectTypesSupported

    List<String> idTokenAlgValuesSupported
}
