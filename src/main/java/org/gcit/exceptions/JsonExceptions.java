package org.gcit.exceptions;

/**
 * JsonExceptions is a custom exception that represents errors related to JSON operations.
 * It extends the BaseException class to inherit basic exception functionalities and
 * provides constructors for creating instances with error messages and causes.
 *
 * JsonExceptions can be used to signal various JSON-related issues, such as parsing errors,
 * data mapping issues, and file handling problems.
 *
 * This class defines two constructors:
 * - JsonExceptions(String message): Constructs a new JsonExceptions instance with the specified detail message.
 * - JsonExceptions(String message, Throwable cause): Constructs a new JsonExceptions instance with the specified
 *   detail message and cause.
 */

@SuppressWarnings("serial")
public class JsonExceptions extends BaseException {
    public JsonExceptions(String message) {
        super(message);
    }

    public JsonExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
