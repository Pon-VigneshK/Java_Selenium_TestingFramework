package org.gcit.exceptions;

/**
 * Custom exception for errors related to SQL (Structured Query Language) operations or database connectivity.
 * <p>
 * This exception extends {@link BaseException} and is designed to be thrown when issues occur during:
 * <ul>
 *   <li>Establishing a connection to a database.</li>
 *   <li>Executing SQL queries (e.g., SELECT, INSERT, UPDATE, DELETE).</li>
 *   <li>Processing {@link java.sql.ResultSet} objects.</li>
 *   <li>Managing database transactions.</li>
 *   <li>Other general database interaction problems.</li>
 * </ul>
 * It is a {@link RuntimeException}, making it an unchecked exception.
 * The underlying cause is often a {@link java.sql.SQLException}.
 *
 * @see BaseException
 * @see java.sql.SQLException
 * @see org.gcit.utils.DataBaseConnectionUtils
 */
@SuppressWarnings("serial") // Indicates that this class doesn't need a custom serialVersionUID
public class SQLConnectionException extends BaseException {
    /**
     * Constructs a new {@code SQLConnectionException} with the specified detail message.
     * The cause is not initialized.
     *
     * @param message The detail message which is saved for later retrieval by the
     *                {@link #getMessage()} method. It should describe the nature of the SQL/database error.
     */
    public SQLConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code SQLConnectionException} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method). It should describe the nature of the SQL/database error.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     *                This is typically the original {@link java.sql.SQLException}.
     */
    public SQLConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
