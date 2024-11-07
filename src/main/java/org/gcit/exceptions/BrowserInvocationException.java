package org.gcit.exceptions;

/**
 * BrowserInvocationException is a custom exception that extends BaseException.
 * It represents an error condition specific to browser invocation failures.
 * This exception can be thrown when the WebDriver instance cannot be created or invoked correctly.
 *
 * This class provides two constructors:
 * - BrowserInvocationException(String message): Constructs an exception with the specified detail message.
 * - BrowserInvocationException(String message, Throwable cause): Constructs an exception with the specified detail message and cause.
 */
@SuppressWarnings("serial")
public class BrowserInvocationException extends BaseException {
    public BrowserInvocationException(String message) {
        super(message);
    }

    public BrowserInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
