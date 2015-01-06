package auth.exceptions

import common.exceptions.EventLogBase

enum AuthEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------

    UserNotFound(0, 404, EventLogBase.EventType.InvalidInput,
            'User with email \'%1$s\' is not found.'),

    UserAlreadyExists(1, 406, EventLogBase.EventType.InvalidInput,
            'User with email \'%1$s\' already exists.'),

    EmailIsNullOrEmpty(2, 400, EventLogBase.EventType.InvalidInput,
            'Requested user\'s email shouldn\'t be null or empty.'),

    EmptyLoginEmailOrPassword(3, 400, EventLogBase.EventType.InvalidInput,
            'To login user\'s email and password should present.'),

    InvalidCredentials(4, 401, EventLogBase.EventType.InvalidInput,
            'Invalid user name (\'%1$s\') or password'),

    TokenNotFound(5, 404, EventLogBase.EventType.InvalidInput,
            'Token with tokenKey \'%1$s\' is not found.'),

    TokenAlreadyExists(6, 406, EventLogBase.EventType.InvalidInput,
            'Token with tokenKey \'%1$s\' already exists.'),

    InvalidPageNumber(7, 406, EventLogBase.EventType.InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    GroupAlreadyExists(8, 406, EventLogBase.EventType.InvalidInput,
            'Group with code \'%1$s\' already exists.'),

    GroupNotFound(9, 404, EventLogBase.EventType.InvalidInput,
            'Group with code \'%1$s\' is not found.'),

    ClientNotFound(10, 404, EventLogBase.EventType.InvalidInput,
            'Client with ID \'%1$s\' is not found.'),

    ClientAlreadyExists(11, 406, EventLogBase.EventType.InvalidInput,
            'Client with ID \'%1$s\' already exists.'),

    OperationNotAllowed(12, 405, EventLogBase.EventType.InvalidInput,
            'Operation is not allowed.')

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
