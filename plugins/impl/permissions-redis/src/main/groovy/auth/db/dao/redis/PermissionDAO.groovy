package auth.db.dao.redis

import auth.db.dao.BasePermissionDAO
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * Created by Tiger on 20.01.15.
 */
class PermissionDAO extends BasePermissionDAO {

    StringRedisTemplate redis

    @Override
    Set<String> getGroupsPermissionsUnion(final Set<String> codes, final String clientID) {
        return redis.opsForSet().union(null, codes.collect { "client:$clientID:group:$it:scopes" as String } as Set)
    }

    @Override
    Set<String> getGroupPermissions(final String code, final String clientID) {
        return redis.opsForSet().members("client:$clientID:group:$code:scopes" as String)
    }

    @Override
    void grantPermissionsToGroup(final String code, final String clientID, final Set<String> scopes) {
        redis.opsForSet().add("client:$clientID:group:$code:scopes" as String, scopes.toArray() as String[])
    }

    @Override
    void revokeGroupPermissions(final String code, final String clientID, final Set<String> scopes) {
        redis.opsForSet().remove("client:$clientID:group:$code:scopes" as String, scopes.toArray() as String[])
    }

    @Override
    Set<String> getAccountPermissions(final String email, final String clientID) {
        return redis.opsForSet().members("client:$clientID:account:$email:scopes" as String)
    }

    @Override
    void grantPermissionsToAccount(final String email, final String clientID, final Set<String> scopes) {
        redis.opsForSet().add("client:$clientID:account:$email:scopes" as String, scopes.toArray() as String[])
    }

    @Override
    void revokeAccountPermissions(final String email, final String clientID, final Set<String> scopes) {
        redis.opsForSet().remove("client:$clientID:account:$email:scopes" as String, scopes.toArray() as String[])
    }

    @Override
    void deleteClientPermissions(final String clientID) {
        redis.delete(redis.keys("client:$clientID:*:*:scopes" as String))
    }

    @Override
    void deleteGroupPermissions(final String code) {
        redis.delete(redis.keys("client:*:group:$code:scopes" as String))
    }

    @Override
    void deleteAccountPermissions(final String email) {
        redis.delete(redis.keys("client:*:account:$email:scopes" as String))
    }
}
