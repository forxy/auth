package auth.api.v1

import common.pojo.SimpleJacksonDateDeserializer
import common.pojo.SimpleJacksonDateSerializer
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.jackson.map.annotate.JsonDeserialize
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@EqualsAndHashCode
@Document(collection = 'scopes_group')
class Group implements Serializable {

    @Id
    String code

    @Indexed(unique = true)
    String name

    String description

    Map<String, List<String>> scopes = new HashMap<>()

    Date updateDate = new Date()
    
    String updatedBy

    Date createDate

    String createdBy

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    Date getUpdateDate() {
        return updateDate
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    void setUpdateDate(final Date updateDate) {
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
