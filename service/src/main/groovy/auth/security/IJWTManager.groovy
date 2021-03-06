package auth.security

import auth.api.v1.Account
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSObject
import net.minidev.json.JSONObject

import java.text.ParseException

/**
 * JSON Web Token Manager
 */
interface IJWTManager {

    String toJWT(Account account) throws JOSEException

    String toJWT(JSONObject json) throws JOSEException

    JWSObject fromJWT(String jwt) throws ParseException, JOSEException
}
