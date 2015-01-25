package auth.db.dao.redis

import auth.api.v1.Group
import auth.api.v1.User
import auth.db.dao.BasePermissionDAO
import auth.db.dao.IUserDAO
import common.status.api.ComponentStatus
import common.status.api.StatusType
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * Created by Tiger on 20.01.15.
 */
class PermissionDAO extends BasePermissionDAO {

    IUserDAO userDAO

    StringRedisTemplate redis

    @Override
    Set<String> getGroupsPermissionsUnion(final Set<String> codes, final String resourceClientID) {
        return redis.opsForSet().union(null, codes.collect {
            "resource:$resourceClientID:group:$it:scopes" as String
        } as Set)
    }

    @Override
    Set<String> getGroupPermissions(final String code, final String resourceClientID) {
        return redis.opsForSet().members("resource:$resourceClientID:group:$code:scopes" as String)
    }

    @Override
    void grantPermissionsToGroup(final String code, final String resourceClientID, final Set<String> scopes) {
        if (scopes) {
            redis.opsForSet().add("resource:$resourceClientID:group:$code:scopes" as String, scopes.toArray() as String[])
        }
    }

    @Override
    void revokeGroupPermissions(final String code, final String resourceClientID, final Set<String> scopes) {
        if (scopes) {
            redis.opsForSet().remove("resource:$resourceClientID:group:$code:scopes" as String, scopes.toArray() as String[])
        }
    }

    @Override
    Set<String> getAccountPermissions(final String email, final String resourceClientID) {
        return redis.opsForSet().members("resource:$resourceClientID:account:$email:scopes" as String)
    }

    @Override
    void grantPermissionsToAccount(final String email, final String resourceClientID, final Set<String> scopes) {
        if (scopes) {
            redis.opsForSet().add("resource:$resourceClientID:account:$email:scopes" as String, scopes.toArray() as String[])
        }
    }

    @Override
    void revokeAccountPermissions(final String email, final String resourceClientID, final Set<String> scopes) {
        if (scopes) {
            redis.opsForSet().remove("resource:$resourceClientID:account:$email:scopes" as String, scopes.toArray() as String[])
        }
    }

    @Override
    void revokeResourcePermissions(String resourceClientID, Set<String> scopes) {
        if (scopes) {
            redis.keys("resource:$resourceClientID:*:*:scopes" as String)?.each {
                redis.opsForSet().remove(it, scopes.toArray() as String[])
            }
        }
    }

    /**
     * At this particular complex case we need to remove all the permissions and approvals that this group has.
     * But at the same time we need not to remove the permissions assigned to this group user that he has being included
     * into other group that has pretty the same permissions i.e.:
     *
     * user1  -> group1 [read_data, write_data] -> resource1 [read_data, write_data]
     *        -> group2 [read_data]             ->
     *
     * Let's say that user1 has both approved permissions for resource1
     * Then if we will remove group1 we need to remove only write_data permission approved for this user
     *
     * @param groupToRemove entity to remove all the permissions assigned to the all it's users
     */
    @Override
    void deleteGroupPermissions(final Group groupToRemove) {
        Set<String> groupPermissionKeys = redis.keys("resource:*:group:$groupToRemove.code:scopes" as String)

        if (groupToRemove?.members) {

            // retrieve all the permissions granted to removable group
            Map<String, Set<String>> clientScopesForGroup = [:]
            groupPermissionKeys?.each {
                clientScopesForGroup[(it.split(':')[1])] = redis.opsForSet().members(it)
            }
            userDAO?.find(groupToRemove.members)?.each { User owner ->

                // retrieve all the granted to user permissions not related to removable group
                Map<String, Set<String>> clientScopesForUser = [:]
                (owner.groups - groupToRemove.code)?.each { String nonRemovableGroup ->
                    redis.keys("resource:*:group:$nonRemovableGroup:scopes" as String)?.each {
                        String resourceClientID = it.split(':')[1]
                        Set<String> scopes = redis.opsForSet().members(it) ?: []
                        clientScopesForUser[(resourceClientID)] = clientScopesForUser[(resourceClientID)] ?
                                scopes : clientScopesForUser[(resourceClientID)] + scopes
                    }
                }

                // revoke all the permissions that were approved for user excluding non removable ones
                clientScopesForGroup?.each { resourceClientID, scopes ->
                    Set<String> approvalsToRevoke = scopes - clientScopesForUser[resourceClientID]
                    redis.keys("resource:$resourceClientID:owner:$owner.email:client:*:approvals" as String)?.each {
                        redis.opsForSet().remove(it, approvalsToRevoke?.toArray() as String[])
                    }
                }
            }
        }
        redis.delete(groupPermissionKeys)
    }

    @Override
    void deleteAccountPermissions(final String email) {
        redis.delete(redis.keys("resource:*:account:$email:scopes" as String))
    }

    @Override
    void deleteClientPermissions(final String resourceClientID) {
        redis.delete(redis.keys("resource:$resourceClientID:*:*:scopes" as String))
    }

    @Override
    Set<String> getAccountApprovals(final String ownerEmail, final String clientID, final String resourceClientID) {
        return redis.opsForSet().members("resource:$resourceClientID:owner:$ownerEmail:client:$clientID:approvals" as String)
    }

    @Override
    void approveAccountPermissions(
            final String ownerEmail, final String clientID, final String resourceClientID, final Set<String> scopes) {
        if (scopes) {
            redis.opsForSet().add("resource:$resourceClientID:owner:$ownerEmail:client:$clientID:approvals" as String, scopes.toArray() as String[])
        }
    }

    @Override
    void revokeAccountApprovals(
            final String ownerEmail, final String clientID, final String resourceClientID, final Set<String> scopes) {
        if (scopes) {
            redis.opsForSet().remove("resource:$resourceClientID:owner:$ownerEmail:client:$clientID:approvals" as String, scopes.toArray() as String[])
        }
    }

    @Override
    void deleteOwnerApprovals(final String email) {
        redis.delete(redis.keys("resource:*:owner:$email:client:*:approvals" as String))
    }

    @Override
    void deleteClientApprovals(final String clientID) {
        redis.delete(redis.keys("resource:*:owner:*:client:$clientID:approvals" as String))
    }

    @Override
    void deleteResourceApprovals(final String resourceClientID) {
        redis.delete(redis.keys("resource:$resourceClientID:owner:*:client:*:approvals" as String))
    }

    @Override
    ComponentStatus getStatus() {
        String location = null
        StatusType statusType = StatusType.GREEN
        long responseTime = Long.MAX_VALUE
        String exceptionMessage = null
        String exceptionDetails = null
        if (redis?.connectionFactory?.connection?.client) {
            location = "$redis.connectionFactory.connection.client.host:$redis.connectionFactory.connection.client.port"

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
        return new ComponentStatus(
                name: 'Permission DAO',
                location: location,
                status: statusType,
                componentType: ComponentStatus.ComponentType.DB,
                responseTime: responseTime,
                exceptionMessage: exceptionMessage,
                exceptionDetails: exceptionDetails
        )
    }
}
