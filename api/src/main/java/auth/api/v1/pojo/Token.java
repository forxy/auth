package auth.api.v1.pojo;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import common.pojo.SimpleJacksonDateDeserializer;
import common.pojo.SimpleJacksonDateSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "token")
public class Token implements Serializable {

    private static final long serialVersionUID = 8325766446360560392L;

    @Id
    private String tokenKey;

    @Indexed
    private String clientID;

    private String type;

    private String refreshToken;

    private List<String> scopes;

    private UserSubject subject;

    private Long expiresIn;

    @Indexed
    private Date issuedAt = new Date();

    public Token() {
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public UserSubject getSubject() {
        return subject;
    }

    public void setSubject(UserSubject subject) {
        this.subject = subject;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @JsonSerialize(using = SimpleJacksonDateSerializer.class)
    public Date getIssuedAt() {
        return issuedAt;
    }

    @JsonDeserialize(using = SimpleJacksonDateDeserializer.class)
    public void setIssuedAt(final Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if (tokenKey == null) {
            if (other.tokenKey != null) {
                return false;
            }
        } else if (!tokenKey.equals(other.tokenKey)) {
            return false;
        }
        if (clientID == null) {
            if (other.clientID != null) {
                return false;
            }
        } else if (!clientID.equals(other.clientID)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (refreshToken == null) {
            if (other.refreshToken != null) {
                return false;
            }
        } else if (!refreshToken.equals(other.refreshToken)) {
            return false;
        }
        if (scopes == null) {
            if (other.scopes != null) {
                return false;
            }
        } else if (!scopes.equals(other.scopes)) {
            return false;
        }
        if (subject == null) {
            if (other.subject != null) {
                return false;
            }
        } else if (!subject.equals(other.subject)) {
            return false;
        }
        if (expiresIn == null) {
            if (other.expiresIn != null) {
                return false;
            }
        } else if (!expiresIn.equals(other.expiresIn)) {
            return false;
        }
        if (issuedAt == null) {
            if (other.issuedAt != null) {
                return false;
            }
        } else if (!issuedAt.equals(other.issuedAt)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tokenKey == null) ? 0 : tokenKey.hashCode());
        result = prime * result + ((clientID == null) ? 0 : clientID.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((refreshToken == null) ? 0 : refreshToken.hashCode());
        result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result + ((expiresIn == null) ? 0 : expiresIn.hashCode());
        result = prime * result + ((issuedAt == null) ? 0 : issuedAt.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return String.format("{tokenKey=%s, clientID=%s, type=%s, refreshToken=%s, " +
                        "expiresIn=%s, issuedAt=%s, subject=%s, scopes=%s}",
                tokenKey, clientID, type, refreshToken, expiresIn, issuedAt, subject, scopes);
    }
}
