package org.gcit.exceptions;

/**
 * InvalidFilepathException is a custom exception that is thrown when an invalid filepath is encountered in the application.
 * This class extends the BaseException and inherits its properties and methods.
 *
 * The exception provides two constructors:
 *
 * - InvalidFilepathException(String message): Constructs a new exception with the specified detail message.
 * - InvalidFilepathException(String message, Throwable cause): Constructs a new exception with the specified detail message and cause.
 */
@SuppressWarnings("serial")
public class InvalidFilepathException extends BaseException {

    public InvalidFilepathException(String message) {
        super(message);
    }

    public InvalidFilepathException(String message, Throwable cause) {
        super(message, cause);
    }
}
