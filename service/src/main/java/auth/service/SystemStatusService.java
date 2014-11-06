package auth.service;

import auth.db.dao.IClientDAO;
import auth.db.dao.ITokenDAO;
import auth.db.dao.IUserDAO;
import common.status.ISystemStatusService;
import common.status.pojo.ComponentStatus;
import common.status.pojo.StatusType;
import common.status.pojo.SystemStatus;
import common.support.SystemProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic to prepare system status
 */
public class SystemStatusService implements ISystemStatusService {

    private ITokenDAO tokenDAO;
    private IClientDAO clientDAO;
    private IUserDAO userDAO;

    @Override
    public SystemStatus getStatus() {
        StatusType systemStatusType = null;
        List<ComponentStatus> componentStatuses = new ArrayList<ComponentStatus>();
        if (tokenDAO != null && clientDAO != null && userDAO != null) {
            componentStatuses.add(tokenDAO.getStatus());
            componentStatuses.add(clientDAO.getStatus());
            componentStatuses.add(userDAO.getStatus());
        } else {
            systemStatusType = StatusType.RED;
        }

        // @formatter:off
        return new SystemStatus(
                SystemProperties.getServiceName(),
                SystemProperties.getHostAddress(),
                SystemProperties.getServiceVersion(),
                systemStatusType != null ? systemStatusType : getTheWorstStatus(componentStatuses),
                componentStatuses);
        // @formatter:on
    }

    private StatusType getTheWorstStatus(final List<ComponentStatus> componentStatuses) {
        StatusType theWorstStatus = StatusType.GREEN;
        for (ComponentStatus componentStatus : componentStatuses) {
            if (componentStatus.getStatus().ordinal() > theWorstStatus.ordinal()) {
                theWorstStatus = componentStatus.getStatus();
            }
        }
        return theWorstStatus;
    }

    public void setTokenDAO(final ITokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public void setClientDAO(IClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public void setUserDAO(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
