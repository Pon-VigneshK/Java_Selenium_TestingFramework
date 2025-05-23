package org.gcit.exceptions;

/**
 * Custom exception thrown specifically when an expected Excel file is not found at the specified path.
 * <p>
 * This exception extends {@link InvalidFilepathException}, providing a more specific
 * type for errors related to missing Excel files, which are often critical for test data input
 * or other framework operations.
 * <p>
 * Common scenarios include:
 * <ul>
 *   <li>The path provided for an Excel file (e.g., in configuration or constants) is incorrect.</li>
 *   <li>The Excel file has been moved, renamed, or deleted.</li>
 *   <li>File system permission issues prevent access to the Excel file.</li>
 * </ul>
 * It is a {@link RuntimeException} (as it extends {@link BaseException} via {@link InvalidFilepathException}),
 * making it an unchecked exception.
 *
 * @see InvalidFilepathException
 * @see BaseException
 * @see org.gcit.utils.ExcelUtils
 */
@SuppressWarnings("serial") // Indicates that this class doesn't need a custom serialVersionUID
public class ExcelFileNotFoundException extends InvalidFilepathException {
    /**
     * Constructs a new {@code ExcelFileNotFoundException} with the specified detail message.
     * The cause is not initialized.
     *
     * @param message The detail message which is saved for later retrieval by the
     *                {@link #getMessage()} method. It should ideally indicate the path or file that was not found.
     */
    public ExcelFileNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ExcelFileNotFoundException} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method). It should ideally indicate the path or file that was not found.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     *                This is often the original {@link java.io.FileNotFoundException}.
     */
    public ExcelFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
