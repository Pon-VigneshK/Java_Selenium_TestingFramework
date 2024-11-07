package org.gcit.exceptions;

/**
 * SQLConnectionException is a custom exception that is thrown to indicate issues related to SQL connections.
 * This exception extends the BaseException class and provides constructors for exception message and cause.
 *
 * It is used to signify errors that occur during the execution of SQL queries and other database operations.
 */
@SuppressWarnings("serial")
public class SQLConnectionException extends BaseException {
    public SQLConnectionException(String message) {
        super(message);
    }

    public SQLConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
