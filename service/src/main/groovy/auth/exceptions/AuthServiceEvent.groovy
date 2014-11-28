package auth.exceptions

import common.exceptions.EventLogBase
import common.exceptions.EventType

import static common.exceptions.EventLogBase.Level

enum AuthServiceEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    UserNotFound(0, 404, EventType.InvalidInput,
            'User with email \'%1$s\' is not found.'),

    UserAlreadyExists(1, 400, EventType.InvalidInput,
            'User with email \'%1$s\' already exists.'),

    EmailIsNullOrEmpty(2, 400, EventType.InvalidInput,
            'Requested user\'s email shouldn\'t be null or empty.'),

    EmptyLoginEmailOrPassword(3, 400, EventType.InvalidInput,
            'To login user\'s email and password should present.'),

    NotAuthorized(4, 401, EventType.InvalidInput,
            'Invalid user name (\'%1$s\') or password'),

    TokenNotFound(5, 404, EventType.InvalidInput,
            'Token with tokenKey \'%1$s\' is not found.'),

    TokenAlreadyExists(6, 400, EventType.InvalidInput,
            'Token with tokenKey \'%1$s\' already exists.'),

    InvalidPageNumber(7, 400, EventType.InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    GroupAlreadyExists(9, 400, EventType.InvalidInput,
            'Group with code \'%1$s\' already exists.'),

    GroupNotFound(8, 404, EventType.InvalidInput,
            'Group with code \'%1$s\' is not found.'),

    ClientNotFound(8, 404, EventType.InvalidInput,
            'Client with ID \'%1$s\' is not found.'),

    ClientAlreadyExists(9, 400, EventType.InvalidInput,
            'Client with ID \'%1$s\' already exists.')

    static final int BASE_EVENT_LOG_ID = 20000

    private AuthServiceEvent(final int eventID, final int responseID, final EventType eventType,
                             final String formatString) {
        this(eventID, responseID, Level.ERROR, eventType, formatString)
    }

    private AuthServiceEvent(final int eventID, final int responseID, final Level logLevel, final EventType eventType,
                             final String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.responseID = responseID
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}
