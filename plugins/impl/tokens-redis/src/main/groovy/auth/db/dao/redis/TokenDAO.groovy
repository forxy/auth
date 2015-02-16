package auth.db.dao.redis

import auth.db.dao.BaseTokenDAO
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * Data access object based on Redis DB for tokens management.
 */
class TokenDAO extends BaseTokenDAO {

    StringRedisTemplate redis
    String location

    @Override
    void grantAccessCode(final String code,final String clientID,final String email) {
        redis.opsForValue().set("account:$email:client:$clientID:accessCode" as String, code)
    }

    @Override
    String getAccessCode(final String clientID,final String email) {
        return redis.opsForValue().get("account:$email:client:$clientID:accessCode" as String)
    }

    @Override
    List<String> getAccessCodesByClient(final String clientID) {
        return null
    }

    @Override
    List<String> getAccessCodesByAccount(final String email) {
        return null
    }

    @Override
    void deleteAccessCodesByClient(final String clientID) {

    }

    @Override
    void deleteAccessCodesByAccount(final String email) {

    }

    @Override
    void grantToken(final String token, final String code) {
        redis.opsForValue().set("token:$token:accessCode", code);
    }

    @Override
    void grantToken(final String token, final String refreshToken, final String code) {
        grantToken(token, code)
        redis.opsForValue().set("refreshToken:$refreshToken:accessCode", code);
    }

    @Override
    void revokeTokensByAccessCode(final String code) {

    }

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        RedisConnection connection = redis?.connectionFactory?.connection
        try {
            if (connection?.client) {
                location = "$connection.client.host:$connection.client.port"

                long timeStart = new Date().time
                try {
                    redis.randomKey()
                } catch (final Exception e) {
                    exceptionMessage = e.message
                    exceptionDetails = ExceptionUtils.getStackTrace(e)
                    statusType = StatusType.RED
                } finally {
                    responseTime = new Date().time - timeStart
                }
            } else {
                statusType = StatusType.RED
            }
        } finally {
            connection.close()
        }
        return new ComponentStatus(
                name: 'Token DAO',
                location: location,
                status: statusType,
                componentType: ComponentStatus.ComponentType.DB,
                responseTime: responseTime,
                exceptionMessage: exceptionMessage,
                exceptionDetails: exceptionDetails
        )
    }
}
