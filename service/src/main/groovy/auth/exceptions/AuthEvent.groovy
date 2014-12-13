package auth.exceptions

import common.exceptions.EventLogBase

enum AuthEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    UserNotFound(0, 404, EventLogBase.EventType.InvalidInput,
            'User with email \'%1$s\' is not found.'),

    UserAlreadyExists(1, 400, EventLogBase.EventType.InvalidInput,
            'User with email \'%1$s\' already exists.'),

    EmailIsNullOrEmpty(2, 400, EventLogBase.EventType.InvalidInput,
            'Requested user\'s email shouldn\'t be null or empty.'),

    EmptyLoginEmailOrPassword(3, 400, EventLogBase.EventType.InvalidInput,
            'To login user\'s email and password should present.'),

    NotAuthorized(4, 401, EventLogBase.EventType.InvalidInput,
            'Invalid user name (\'%1$s\') or password'),

    TokenNotFound(5, 404, EventLogBase.EventType.InvalidInput,
            'Token with tokenKey \'%1$s\' is not found.'),

    TokenAlreadyExists(6, 400, EventLogBase.EventType.InvalidInput,
            'Token with tokenKey \'%1$s\' already exists.'),

    InvalidPageNumber(7, 400, EventLogBase.EventType.InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    GroupAlreadyExists(9, 400, EventLogBase.EventType.InvalidInput,
            'Group with code \'%1$s\' already exists.'),

    GroupNotFound(8, 404, EventLogBase.EventType.InvalidInput,
            'Group with code \'%1$s\' is not found.'),

    ClientNotFound(8, 404, EventLogBase.EventType.InvalidInput,
            'Client with ID \'%1$s\' is not found.'),

    ClientAlreadyExists(9, 400, EventLogBase.EventType.InvalidInput,
            'Client with ID \'%1$s\' already exists.')

    static int BASE_EVENT_LOG_ID = 10000

    private AuthEvent(int eventID, int responseID, EventLogBase.EventType eventType, String formatString) {
        this(eventID, responseID, EventLogBase.Level.ERROR, eventType, formatString)
    }

    private AuthEvent(int eventID, int httpCode, EventLogBase.Level logLevel, EventLogBase.EventType eventType,
                      String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}
