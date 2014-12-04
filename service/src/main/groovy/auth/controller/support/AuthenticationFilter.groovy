package auth.controller.support

import auth.api.v1.Group
import auth.api.v1.User
import auth.security.IJWTManager
import auth.service.IGroupService
import auth.service.IUserService
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSObject
import common.rest.client.transport.support.ObjectMapperProvider
import net.minidev.json.JSONObject
import org.apache.cxf.common.security.SimplePrincipal
import org.codehaus.jackson.map.ObjectMapper

import javax.annotation.Priority
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext
import javax.ws.rs.ext.Provider
import java.lang.reflect.Method
import java.security.Principal
import java.text.ParseException

@Provider
@Priority(Priorities.AUTHENTICATION)
class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    HttpHeaders headers

    @Context
    ResourceInfo resourceInfo

    IUserService userManager

    IGroupService groupManager

    IJWTManager jwtManager

    List<String> exclude

    private ObjectMapper mapper = ObjectMapperProvider.getDefaultMapper()

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        if (exclude != null && exclude.contains(requestContext.getUriInfo().getPath())) {
            return
        }
        SecurityContext sc = requestContext.getSecurityContext()
        if (sc != null) {
            Principal principal = sc.getUserPrincipal()
            if (principal != null) {
                String accountName = principal.getName()

                final User account = userManager.getUser(accountName)
                if (account == null) {
                    requestContext.abortWith(createFaultResponse())
                } else {
                    setNewSecurityContext(requestContext, account)
                    return
                }
            }
        }
        List<String> authValues = headers.getRequestHeader('Authorization')
        if (authValues == null || authValues.size() != 1) {
            requestContext.abortWith(createFaultResponse())
            return
        }
        String[] values = authValues.get(0).split(' ')
        if (values.length != 2 || !'Bearer'.equals(values[0])) {
            requestContext.abortWith(createFaultResponse())
            return
        }

        try {
            JWSObject jwt = jwtManager.fromJWT(values[1])
            JSONObject jwtBody = jwt.getPayload().toJSONObject()
            final User account = userManager.getUser((String) jwtBody.get('sub'))

            setNewSecurityContext(requestContext, account)

            // Get method invoked.
            Method methodInvoked = resourceInfo.getResourceMethod()

            if (methodInvoked.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAllowedAnnotation = methodInvoked.getAnnotation(RolesAllowed.class)
                Set<String> rolesAllowed = new HashSet<>(Arrays.asList(rolesAllowedAnnotation.value()))
                sc = requestContext.getSecurityContext()
                for (String role : rolesAllowed) {
                    if (sc.isUserInRole(role)) {
                        return
                    }
                }
                requestContext.abortWith(createFaultResponse())
            }

        } catch (ParseException | JOSEException ignored) {
            requestContext.abortWith(createFaultResponse())
        }
    }

    void setNewSecurityContext(final ContainerRequestContext requestContext, final User account) {
        requestContext.setSecurityContext(new SecurityContext() {
            User user = account
            Principal userPrincipal = new SimplePrincipal(account.getEmail())

            @Override
            Principal getUserPrincipal() {
                return userPrincipal
            }

            @Override
            boolean isUserInRole(String role) {
                if (user.getGroups() != null) {
                    for (Group group : groupManager.getGroups(user.getGroups())) {
                        if (group.getScopes() != null) {
                            for (Map.Entry<String, List<String>> clientScopes : group.getScopes().entrySet()) {
                                if (clientScopes.getValue().contains(role)) {
                                    return true
                                }
                            }
                        }
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
        })
    }

    static Response createFaultResponse() {
        return Response.status(401).header('WWW-Authenticate', 'Basic realm=\'Forxy.ru\'').build()
        //return Response.status(301).header('location', '/AuthService/app/login').build()
    }
}
