package org.gcit.exceptions;

/**
 * Custom exception thrown when a filepath provided or used within the application is found to be invalid.
 * <p>
 * This exception extends {@link BaseException} and serves as a more general indicator of issues
 * related to file paths than {@link ExcelFileNotFoundException}. It can be used for scenarios where:
 * <ul>
 *   <li>A file path string is malformed (e.g., contains illegal characters).</li>
 *   <li>A path refers to a location that is not a file when a file is expected, or vice-versa.</li>
 *   <li>A required file or directory at a path does not exist (though specific exceptions like
 *       {@link ExcelFileNotFoundException} might be preferred for certain file types).</li>
 *   <li>Permissions issues prevent access to a path, although this might also manifest as an {@link java.io.IOException} wrapped as a cause.</li>
 * </ul>
 * It is a {@link RuntimeException}, making it an unchecked exception.
 *
 * @see BaseException
 * @see ExcelFileNotFoundException
 */
@SuppressWarnings("serial") // Indicates that this class doesn't need a custom serialVersionUID
public class InvalidFilepathException extends BaseException {

    /**
     * Constructs a new {@code InvalidFilepathException} with the specified detail message.
     * The cause is not initialized.
     *
     * @param message The detail message which is saved for later retrieval by the
     *                {@link #getMessage()} method. It should ideally include the problematic filepath.
     */
    public InvalidFilepathException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code InvalidFilepathException} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method). It should ideally include the problematic filepath.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     *                This could be an {@link java.io.IOException} or other relevant exception.
     */
    public InvalidFilepathException(String message, Throwable cause) {
        super(message, cause);
    }
}
