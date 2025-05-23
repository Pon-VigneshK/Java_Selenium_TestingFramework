package org.gcit.exceptions;

/**
 * Custom exception for errors encountered while accessing or processing configuration properties files.
 * <p>
 * This exception extends {@link BaseException} and is typically thrown in scenarios such as:
 * <ul>
 *   <li>The properties file (e.g., {@code config.properties}) cannot be found or read.</li>
 *   <li>A required property key is missing from the properties file.</li>
 *   <li>An error occurs during the loading or parsing of the properties file content.</li>
 * </ul>
 * It is a {@link RuntimeException}, making it an unchecked exception.
 *
 * @see BaseException
 * @see org.gcit.utils.PropertyUtils
 * @see java.util.Properties
 */
@SuppressWarnings("serial") // Indicates that this class doesn't need a custom serialVersionUID
public class PropertiesFileException extends BaseException {
    /**
     * Constructs a new {@code PropertiesFileException} with the specified detail message.
     * The cause is not initialized.
     *
     * @param message The detail message which is saved for later retrieval by the
     *                {@link #getMessage()} method. It should describe the nature of the properties file error.
     */
    public PropertiesFileException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code PropertiesFileException} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method). It should describe the nature of the properties file error.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     *                This is often an {@link java.io.IOException} if the file itself cannot be accessed.
     */
    public PropertiesFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
