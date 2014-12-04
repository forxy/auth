package auth.exceptions

import common.exceptions.EventLogBase

import static common.exceptions.EventLogBase.EventType
import static common.exceptions.EventLogBase.EventType.InvalidInput
import static common.exceptions.EventLogBase.Level

enum AuthServiceEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    UserNotFound(0, 404, InvalidInput,
            'User with email \'%1$s\' is not found.'),

    UserAlreadyExists(1, 400, InvalidInput,
            'User with email \'%1$s\' already exists.'),

    EmailIsNullOrEmpty(2, 400, InvalidInput,
            'Requested user\'s email shouldn\'t be null or empty.'),

    EmptyLoginEmailOrPassword(3, 400, InvalidInput,
            'To login user\'s email and password should present.'),

    NotAuthorized(4, 401, InvalidInput,
            'Invalid user name (\'%1$s\') or password'),

    TokenNotFound(5, 404, InvalidInput,
            'Token with tokenKey \'%1$s\' is not found.'),

    TokenAlreadyExists(6, 400, InvalidInput,
            'Token with tokenKey \'%1$s\' already exists.'),

    InvalidPageNumber(7, 400, InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    GroupAlreadyExists(9, 400, InvalidInput,
            'Group with code \'%1$s\' already exists.'),

    GroupNotFound(8, 404, InvalidInput,
            'Group with code \'%1$s\' is not found.'),

    ClientNotFound(8, 404, InvalidInput,
            'Client with ID \'%1$s\' is not found.'),

    ClientAlreadyExists(9, 400, InvalidInput,
            'Client with ID \'%1$s\' already exists.')

    static int BASE_EVENT_LOG_ID = 10000

    private AuthServiceEvent(int eventID, int responseID, EventType eventType, String formatString) {
        this(eventID, responseID, Level.ERROR, eventType, formatString)
    }

    private AuthServiceEvent(int eventID, int httpCode, Level logLevel, EventType eventType, String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}
