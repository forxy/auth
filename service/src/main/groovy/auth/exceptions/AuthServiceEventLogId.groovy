package auth.exceptions

import common.exceptions.EventLogBase
import common.exceptions.EventType

enum AuthServiceEventLogID implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    UserNotFound(BASE_EVENT_LOG_ID, 404,
            'User with email \'%1$s\' is not found.',
            EventType.InvalidInput),

    UserAlreadyExists(BASE_EVENT_LOG_ID + 1, 400,
            'User with email \'%1$s\' already exists.',
            EventType.InvalidInput),

    EmailIsNullOrEmpty(BASE_EVENT_LOG_ID + 2, 400,
            'Requested user\'s email shouldn\'t be null or empty.',
            EventType.InvalidInput),

    EmptyLoginEmailOrPassword(BASE_EVENT_LOG_ID + 3, 400,
            'To login user\'s email and password should present.',
            EventType.InvalidInput),

    NotAuthorized(BASE_EVENT_LOG_ID + 4, 401,
            'Invalid user name (\'%1$s\') or password', EventType.InvalidInput),

    TokenNotFound(BASE_EVENT_LOG_ID + 5, 404,
            'Token with tokenKey \'%1$s\' is not found.',
            EventType.InvalidInput),

    TokenAlreadyExists(BASE_EVENT_LOG_ID + 6, 400,
            'Token with tokenKey \'%1$s\' already exists.',
            EventType.InvalidInput),

    InvalidPageNumber(BASE_EVENT_LOG_ID + 7, 400,
            'Invalid page number provided: \'%1$s\'',
            EventType.InvalidInput),

    GroupAlreadyExists(BASE_EVENT_LOG_ID + 9, 400,
            'Group with code \'%1$s\' already exists.',
            EventType.InvalidInput),

    GroupNotFound(BASE_EVENT_LOG_ID + 8, 404,
            'Group with code \'%1$s\' is not found.',
            EventType.InvalidInput),

    ClientNotFound(BASE_EVENT_LOG_ID + 8, 404,
            'Client with ID \'%1$s\' is not found.',
            EventType.InvalidInput),

    ClientAlreadyExists(BASE_EVENT_LOG_ID + 9, 400,
            'Client with ID \'%1$s\' already exists.',
            EventType.InvalidInput)

    static final int BASE_EVENT_LOG_ID = 20000

    EventLogBase.Level logLevel
    String formatString
    int eventID
    int responseID
    EventType eventType

    AuthServiceEventLogID(final int eventID, final int responseID, final String formatString,
                          final EventType eventType) {
        this(eventID, responseID, EventLogBase.Level.ERROR, formatString, eventType)
    }

    AuthServiceEventLogID(final int eventID, final int responseID, final EventLogBase.Level logLevel,
                          final String formatString, final EventType eventType) {
        this.eventID = eventID
        this.responseID = responseID
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }

    String getMessage(final Object... arguments) {
        return arguments != null && arguments.length > 0 ? String.format(formatString, arguments) : formatString
    }
}
