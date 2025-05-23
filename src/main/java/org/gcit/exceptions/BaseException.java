package org.gcit.exceptions;

/**
 * A custom runtime exception that serves as the base class for most application-specific
 * exceptions within this framework.
 * <p>
 * Extending {@link RuntimeException} means these are unchecked exceptions. This class
 * provides constructors to set a detail message and an optional underlying cause.
 * It is intended to be subclassed by more specific exception types that represent
 * particular error conditions (e.g., {@link ExcelFileNotFoundException}, {@link BrowserInvocationException}).
 * Using a common base exception allows for more organized error handling and categorization
 * of framework-specific issues.
 *
 * @see RuntimeException
 */
public class BaseException extends RuntimeException {
    /**
     * Constructs a new {@code BaseException} with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause(Throwable)}.
     *
     * @param message The detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code BaseException} with the specified detail message and cause.
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this runtime exception's detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
