package org.gcit.exceptions;

@SuppressWarnings("serial")
public class JsonExceptions extends BaseException {
    public JsonExceptions(String message) {
        super(message);
    }

    public JsonExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
