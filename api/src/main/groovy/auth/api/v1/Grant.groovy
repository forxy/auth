package auth.api.v1

/**
 * Permissions mapping for the particular group-client combination
 */
class Grant {
    String group
    String client
    Set<String> scopes
}
