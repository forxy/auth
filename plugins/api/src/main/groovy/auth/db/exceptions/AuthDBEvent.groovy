package auth.db.exceptions

import common.exceptions.EventLogBase

enum AuthDBEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Database events
    // -------------------------------------------------------------------

    UserAlreadyExists(1000, 406, EventLogBase.EventType.InvalidInput,
            'User with login \'%1$s\' already exists.'),

    UserNotFound(1001, 404, EventLogBase.EventType.InvalidInput,
            'User with login \'%1$s\' is not found.'),

    GroupAlreadyExists(1002, 406, EventLogBase.EventType.InvalidInput,
            'Group with code \'%1$s\' already exists.'),

    GroupNotFound(1003, 404, EventLogBase.EventType.InvalidInput,
            'Group with code \'%1$s\' is not found.'),

    ClientAlreadyExists(1004, 406, EventLogBase.EventType.InvalidInput,
            'Client with ID=\'%1$s\' already exists.'),

    ClientNotFound(1005, 404, EventLogBase.EventType.InvalidInput,
            'Client with ID=\'%1$s\' is not found.')

    static int BASE_EVENT_LOG_ID = 10000

    private AuthDBEvent(int eventID, int responseID, EventLogBase.EventType eventType, String messageFormat) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, messageFormat)
    }

    private AuthDBEvent(int eventID, int httpCode, EventLogBase.Level logLevel, EventLogBase.EventType eventType,
                        String messageFormat) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.messageFormat = messageFormat
        this.eventType = eventType
    }
}
