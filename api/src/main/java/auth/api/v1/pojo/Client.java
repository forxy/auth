package auth.api.v1.pojo;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import common.pojo.SimpleJacksonDateDeserializer;
import common.pojo.SimpleJacksonDateSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "client")
public class Client implements Serializable {

    private static final long serialVersionUID = -6795472289012239081L;

    @Id
    private String clientID;
    private String secret;

    @Indexed(unique = true)
    private String name;
    private String description;
    private String webUri;
    private List<String> redirectUris = new ArrayList<>();
    private List<String> scopes = new ArrayList<>();
    private List<String> audiences = new ArrayList<>();

    private Date updateDate = new Date();

    private String updatedBy;

    private Date createDate;

    private String createdBy;

    public Client() {}

    public Client(final String clientID, final String name, final String updatedBy, final String createdBy) {
        this.clientID = clientID;
        this.name = name;
        this.updatedBy = updatedBy;
        this.createdBy = createdBy;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(final String clientID) {
        this.clientID = clientID;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getWebUri() {
        return webUri;
    }

    public void setWebUri(final String webUri) {
        this.webUri = webUri;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(final List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(final List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getAudiences() {
        return audiences;
    }

    public void setAudiences(final List<String> audiences) {
        this.audiences = audiences;
    }

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    public Date getUpdateDate() {
        return updateDate;
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    public void setUpdateDate(final Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (clientID == null) {
            if (other.clientID != null) {
                return false;
            }
        } else if (!clientID.equals(other.clientID)) {
            return false;
        }
        if (secret == null) {
            if (other.secret != null) {
                return false;
            }
        } else if (!secret.equals(other.secret)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (webUri == null) {
            if (other.webUri != null) {
                return false;
            }
        } else if (!webUri.equals(other.webUri)) {
            return false;
        }
        if (redirectUris == null) {
            if (other.redirectUris != null) {
                return false;
            }
        } else if (!redirectUris.equals(other.redirectUris)) {
            return false;
        }
        if (scopes == null) {
            if (other.scopes != null) {
                return false;
            }
        } else if (!scopes.equals(other.scopes)) {
            return false;
        }
        if (audiences == null) {
            if (other.audiences != null) {
                return false;
            }
        } else if (!audiences.equals(other.audiences)) {
            return false;
        }
        if (updateDate == null) {
            if (other.updateDate != null) {
                return false;
            }
        } else if (!updateDate.equals(other.updateDate)) {
            return false;
        }
        if (updatedBy == null) {
            if (other.updatedBy != null) {
                return false;
            }
        } else if (!updatedBy.equals(other.updatedBy)) {
            return false;
        }
        if (createDate == null) {
            if (other.createDate != null) {
                return false;
            }
        } else if (!createDate.equals(other.createDate)) {
            return false;
        }
        if (createdBy == null) {
            if (other.createdBy != null) {
                return false;
            }
        } else if (!createdBy.equals(other.createdBy)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientID == null) ? 0 : clientID.hashCode());
        result = prime * result + ((secret == null) ? 0 : secret.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((webUri == null) ? 0 : webUri.hashCode());
        result = prime * result + ((redirectUris == null) ? 0 : redirectUris.hashCode());
        result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
        result = prime * result + ((audiences == null) ? 0 : audiences.hashCode());
        result = prime * result + ((updateDate == null) ? 0 : updateDate.hashCode());
        result = prime * result + ((updatedBy == null) ? 0 : updatedBy.hashCode());
        result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
        result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("{clientID=%s, name=%s, description=%s, webUri=%s, updateDate=%s, updatedBy=%s, " +
                        "createDate=%s, createdBy=%s, redirectUris=%s, scopes=%s, audiences=%s}",
                clientID, name, description, webUri, updateDate, updatedBy, createDate,
                createdBy, redirectUris, scopes, audiences);
    }
}
