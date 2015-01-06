package auth.controller.support

import auth.api.v1.Group
import auth.api.v1.User
import auth.security.IJWTManager
import auth.service.IGroupService
import auth.service.IUserService
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSObject
import common.exceptions.HttpEvent
import common.exceptions.ServiceException
import net.minidev.json.JSONObject
import org.apache.cxf.common.security.SimplePrincipal

import javax.annotation.Priority
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.ext.Provider
import java.lang.reflect.Method
import java.security.Principal
import java.text.ParseException

@Provider
@Priority(Priorities.AUTHENTICATION)
class AuthFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo

    @Context
    HttpHeaders headers

    IUserService userService

    IGroupService groupService

    IJWTManager jwtManager

    @Override
    void filter(final ContainerRequestContext requestContext) throws IOException {

        Method methodInvoked = resourceInfo.resourceMethod

        if (!methodInvoked.isAnnotationPresent(RolesAllowed.class)) {
            return
        }
        SecurityContext sc = requestContext.securityContext
        if (sc != null) {
            Principal principal = sc.userPrincipal
            if (principal != null) {
                String accountName = principal.name

                final User account = userService.getUser(accountName)
                if (account == null) {
                    throw new ServiceException(HttpEvent.Unauthorized)
                } else {
                    requestContext.securityContext = getSecurityContext(account, groupService)
                    return
                }
            }
        }

        List<String> authValues = headers.getRequestHeader('Authorization')
        if (authValues == null || authValues.size() != 1) {
            throw new ServiceException(HttpEvent.Unauthorized)
        }
        String[] values = authValues.get(0).split(' ')
        if (values.length != 2 || !'Bearer'.equals(values[0])) {
            throw new ServiceException(HttpEvent.Unauthorized)
        }

        try {
            JWSObject jwt = jwtManager.fromJWT(values[1])
            JSONObject jwtBody = jwt.payload.toJSONObject()
            final User account = userService.getUser((String) jwtBody.get('sub'))

            requestContext.securityContext = getSecurityContext(account, groupService)
            // Get method invoked.
            RolesAllowed rolesAllowedAnnotation = methodInvoked.getAnnotation(RolesAllowed.class)
            Set<String> rolesAllowed = rolesAllowedAnnotation.value()?.toList()?.toSet()
            sc = requestContext.securityContext

            if (rolesAllowed?.find { sc?.isUserInRole(it) }) return

            throw new ServiceException(HttpEvent.AccessDenied)

        } catch (ParseException | JOSEException ignored) {
            throw new ServiceException(HttpEvent.Unauthorized)
        }
    }

    static SecurityContext getSecurityContext(final User account, final IGroupService groupService) {
        return new AuthSecurityContext(
                user: account,
                userPrincipal: new SimplePrincipal(account.email),
                groupService: groupService
        )
    }

    static class AuthSecurityContext implements SecurityContext {

        User user
        Principal userPrincipal
        IGroupService groupService

        @Override
        boolean isUserInRole(String role) {
            if (user.groups) {
                for (Group group : groupService.getGroups(user.groups)) {
                    if (group?.scopes?.find { client, roles ->
                        roles.contains(role)
                    }) return true
                }
            }
            return false
        }

        @Override
        boolean isSecure() {
            return true
        }

        @Override
        String getAuthenticationScheme() {
            return BASIC_AUTH
        }
    }
}
