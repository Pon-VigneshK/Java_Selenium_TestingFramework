package org.gcit.exceptions;

@SuppressWarnings("serial")
public class SQLConnectionException extends BaseException {
    public SQLConnectionException(String message) {
        super(message);
    }

    public SQLConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
