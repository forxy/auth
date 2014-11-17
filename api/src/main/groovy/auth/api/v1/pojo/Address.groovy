package auth.api.v1.pojo

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class Address {

    String country

    String city

    String addressLine
}
