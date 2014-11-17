package auth.api.v1.pojo

import common.pojo.SimpleJacksonDateDeserializer
import common.pojo.SimpleJacksonDateSerializer
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.map.annotate.JsonDeserialize
import org.codehaus.jackson.map.annotate.JsonSerialize

@ToString
@EqualsAndHashCode
class Profile extends User implements Serializable {

    Date birthDate

    Set<String> telephones

    Address address

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    Date getBirthDate() {
        return birthDate
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate
    }
}
