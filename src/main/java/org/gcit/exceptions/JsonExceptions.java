package org.gcit.exceptions;

/**
 * Custom exception for errors encountered during JSON (JavaScript Object Notation) processing.
 * <p>
 * This exception extends {@link BaseException} and is intended to be thrown when issues arise
 * during operations such as:
 * <ul>
 *   <li>Parsing JSON strings or files (e.g., malformed JSON content).</li>
 *   <li>Mapping JSON data to Java objects (deserialization) or vice-versa (serialization) using libraries like Jackson.</li>
 *   <li>Reading from or writing to JSON files, if the error is specific to the JSON format or structure
 *       (though I/O issues might be wrapped as a cause).</li>
 *   <li>Unexpected JSON structure when specific fields or formats are expected.</li>
 * </ul>
 * It is a {@link RuntimeException}, making it an unchecked exception.
 *
 * @see BaseException
 * @see com.fasterxml.jackson.databind.ObjectMapper
 * @see org.gcit.utils.JsonUtils
 */
@SuppressWarnings("serial") // Indicates that this class doesn't need a custom serialVersionUID
public class JsonExceptions extends BaseException {
    /**
     * Constructs a new {@code JsonExceptions} with the specified detail message.
     * The cause is not initialized.
     *
     * @param message The detail message which is saved for later retrieval by the
     *                {@link #getMessage()} method. It should describe the nature of the JSON processing error.
     */
    public JsonExceptions(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code JsonExceptions} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method). It should describe the nature of the JSON processing error.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     *                This is often an exception from the underlying JSON processing library (e.g., Jackson).
     */
    public JsonExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
