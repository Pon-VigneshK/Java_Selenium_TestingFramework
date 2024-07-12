package org.gcit.exceptions;

public class BrowserInvocationException extends BaseException {
    public BrowserInvocationException(String message) {
        super(message);
    }

    public BrowserInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
