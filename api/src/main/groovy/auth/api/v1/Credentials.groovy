package auth.api.v1

import groovy.transform.Canonical

@Canonical
public class Credentials {
    String login
    String password
}
