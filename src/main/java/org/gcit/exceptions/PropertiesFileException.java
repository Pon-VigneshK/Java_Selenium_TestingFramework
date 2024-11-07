package org.gcit.exceptions;

/**
 * PropertiesFileException is a custom exception thrown when there is an issue
 * with accessing or processing properties in a properties file.
 *
 * This exception extends BaseException and provides two constructors for
 * creating an exception instance with a detailed message or with a
 * detailed message and a cause.
 *
 * Constructors:
 * - PropertiesFileException(String message): Creates a new exception with the specified detail message.
 * - PropertiesFileException(String message, Throwable cause): Creates a new exception with the specified
 *   detail message and cause.
 */
@SuppressWarnings("serial")
public class PropertiesFileException extends BaseException {
    public PropertiesFileException(String message) {
        super(message);
    }

    public PropertiesFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
