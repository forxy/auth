package auth.api.v1.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON Web Token body
 */
public class JWT {

    @JsonProperty("iss")
    private String issuer;

    @JsonProperty("sub")
    private String subject;

    @JsonProperty("aud")
    private String audience;

    @JsonProperty("exp")
    private Long expirationTime;

    @JsonProperty("nbf")
    private Long notBefore;

    @JsonProperty("iat")
    private Long issuedAt;

    @JsonProperty("jti")
    private String jwtID;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Long notBefore) {
        this.notBefore = notBefore;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getJwtID() {
        return jwtID;
    }

    public void setJwtID(String jwtID) {
        this.jwtID = jwtID;
    }
}
