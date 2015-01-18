package auth.api.v1

import groovy.transform.Canonical

/**
 * OpenID configuration
 */
@Canonical
class DiscoveryInfo {

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
