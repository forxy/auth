package auth.api.v1.pojo

import common.pojo.SimpleJacksonDateDeserializer
import common.pojo.SimpleJacksonDateSerializer
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.map.annotate.JsonDeserialize
import org.codehaus.jackson.map.annotate.JsonSerialize

@ToString
@EqualsAndHashCode
class UserSubject implements Serializable {

    String userID

    List<String> roles

    Map<String, String> properties = new HashMap<String, String>()

    Date updateDate = new Date()

    String updatedBy

    Date createDate = new Date()

    String createdBy

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    Date getUpdateDate() {
        return updateDate
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate
    }

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    Date getCreateDate() {
        return createDate
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    void setCreateDate(final Date createDate) {
        this.createDate = createDate
    }
}
