package org.gcit.exceptions;

public class InvalidFilepathException extends BaseException {

    public InvalidFilepathException(String message) {
        super(message);
    }

    public InvalidFilepathException(String message, Throwable cause) {
        super(message, cause);
    }
}
