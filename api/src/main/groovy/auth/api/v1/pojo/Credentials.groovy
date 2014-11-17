package auth.api.v1.pojo

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
public class Credentials implements Serializable {

    String email

    String password
}
