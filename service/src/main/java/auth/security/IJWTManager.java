package auth.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import net.minidev.json.JSONObject;
import auth.api.v1.pojo.User;

import java.text.ParseException;

/**
 * JSON Web Token Manager
 */
public interface IJWTManager {

    String toJWT(User user) throws JOSEException;

    String toJWT(JSONObject json) throws JOSEException;

    JWSObject fromJWT(String jwt) throws ParseException, JOSEException;
}
