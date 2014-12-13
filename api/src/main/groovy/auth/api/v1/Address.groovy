package auth.api.v1

import groovy.transform.Canonical

@Canonical
class Address {

    String country

    String city

    String addressLine
}
