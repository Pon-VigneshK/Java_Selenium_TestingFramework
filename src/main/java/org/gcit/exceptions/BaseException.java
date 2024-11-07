package org.gcit.exceptions;

/**
 * BaseException is a custom runtime exception that serves as a superclass for application-specific exceptions.
 * It allows for providing an error message and an optional cause for the exception.
 *
 * This class extends RuntimeException and provides two constructors:
 *
 * - BaseException(String message): Constructs a new runtime exception with the specified detail message.
 * - BaseException(String message, Throwable cause): Constructs a new runtime exception with the specified detail message
 *   and cause.
 */
public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
