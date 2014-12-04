package auth.service

import auth.db.dao.IClientDAO
import auth.db.dao.ITokenDAO
import auth.db.dao.IUserDAO
import common.status.ISystemStatusService
import common.status.api.ComponentStatus
import common.status.api.StatusType
import common.status.api.SystemStatus
import common.support.SystemProperties

/**
 * Business logic to prepare system status
 */
class SystemStatusService implements ISystemStatusService {

    ITokenDAO tokenDAO
    IClientDAO clientDAO
    IUserDAO userDAO

    @Override
    SystemStatus getStatus() {
        StatusType systemStatusType = null
        List<ComponentStatus> componentStatuses = new ArrayList<ComponentStatus>()
        if (tokenDAO != null && clientDAO != null && userDAO != null) {
            componentStatuses.add(tokenDAO.getStatus())
            componentStatuses.add(clientDAO.getStatus())
            componentStatuses.add(userDAO.getStatus())
        } else {
            systemStatusType = StatusType.RED
        }

        // @formatter:off
        return new SystemStatus(
                SystemProperties.getServiceName(),
                SystemProperties.getHostAddress(),
                SystemProperties.getServiceVersion(),
                systemStatusType != null ? systemStatusType : getTheWorstStatus(componentStatuses),
                componentStatuses)
        // @formatter:on
    }

    static StatusType getTheWorstStatus(final List<ComponentStatus> componentStatuses) {
        StatusType theWorstStatus = StatusType.GREEN
        for (ComponentStatus componentStatus : componentStatuses) {
            if (componentStatus.getStatus().ordinal() > theWorstStatus.ordinal()) {
                theWorstStatus = componentStatus.getStatus()
            }
        }
        return theWorstStatus
    }
}
