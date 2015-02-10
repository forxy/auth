package auth.api.v1

import groovy.transform.Canonical

/**
 * Generic Auth Account
 */
@Canonical
class Account {
    String email
    String password

    Set<String> groups = []
}
