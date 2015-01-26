package auth.exceptions

import common.exceptions.EventLogBase

enum AuthEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------

    EmailIsNullOrEmpty(2, 400, EventLogBase.EventType.InvalidInput,
            'Requested user\'s email shouldn\'t be null or empty.'),

    EmptyLoginEmailOrPassword(3, 400, EventLogBase.EventType.InvalidInput,
            'To login user\'s email and password should present.'),

    InvalidCredentials(4, 401, EventLogBase.EventType.InvalidInput,
            'Invalid user name (\'%1$s\') or password'),

    InvalidPageNumber(7, 406, EventLogBase.EventType.InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    NotEnoughPermissions(13, 403, EventLogBase.EventType.InvalidInput,
            'Not enough permissions.')

    static int BASE_EVENT_LOG_ID = 10000

    private AuthEvent(int eventID, int responseID, EventLogBase.EventType eventType, String messageFormat) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, messageFormat)
    }

    private AuthEvent(int eventID, int httpCode, EventLogBase.Level logLevel, EventLogBase.EventType eventType,
                      String messageFormat) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.messageFormat = messageFormat
        this.eventType = eventType
    }
}
