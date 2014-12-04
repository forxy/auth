package auth.security

import auth.api.v1.User
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWTClaimsSet
import net.minidev.json.JSONObject
import org.springframework.beans.factory.InitializingBean

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.text.ParseException

/**
 * JSON Web Token Manager
 */
class JWTManager implements IJWTManager, InitializingBean {

    String privateKey

    String clientID

    String publicKey

    RSAPublicKey rsaPublicKey

    RSAPrivateKey rsaPrivateKey

    JWSVerifier rsaVerifier

    @Override
    String toJWT(User user) throws JOSEException {
        JWTClaimsSet jwtClaims = new JWTClaimsSet()
        jwtClaims.setIssuer('http://localhost:11080/AuthService/')
        jwtClaims.setSubject(user.getEmail())
        List<String> aud = new ArrayList<>()
        aud.add('http://localhost:11080/AuthService/')
        jwtClaims.setAudience(aud)

        // Set expiration in 10 minutes
        jwtClaims.setExpirationTime(new Date(new Date().getTime() + 1000 * 60 * 10))
        jwtClaims.setNotBeforeTime(new Date())
        jwtClaims.setIssueTime(new Date())
        jwtClaims.setJWTID(UUID.randomUUID().toString())
        return toJWT(jwtClaims.toJSONObject())
    }

    String toJWT(JSONObject json) throws JOSEException {
        JWSSigner signer = new RSASSASigner(rsaPrivateKey)
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), new Payload(json))
        jwsObject.sign(signer)
        return jwsObject.serialize()
    }

    JWSObject fromJWT(String jwt) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(jwt)
        jwsObject.verify(rsaVerifier)
        return jwsObject
    }

    @Override
    void afterPropertiesSet() throws Exception {
        final KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance('RSA')
        keyGenerator.initialize(1024)

        KeyPair kp = keyGenerator.genKeyPair()
        rsaPublicKey = (RSAPublicKey) kp.getPublic()
        rsaPrivateKey = (RSAPrivateKey) kp.getPrivate()

        rsaVerifier = new RSASSAVerifier(rsaPublicKey)
    }
}
