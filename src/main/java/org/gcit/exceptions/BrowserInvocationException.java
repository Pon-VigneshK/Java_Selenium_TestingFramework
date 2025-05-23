package org.gcit.exceptions;

/**
 * Custom exception thrown when an error occurs during the invocation or setup of a web browser
 * by Selenium WebDriver.
 * <p>
 * This exception extends {@link BaseException} and typically indicates issues such as:
 * <ul>
 *   <li>The WebDriver executable (e.g., chromedriver, geckodriver) is not found or not accessible.</li>
 *   <li>Incorrect browser capabilities are specified.</li>
 *   <li>The browser fails to start or crashes during startup.</li>
 *   <li>Problems connecting to a remote WebDriver (e.g., Selenium Grid).</li>
 * </ul>
 * It is a {@link RuntimeException}, making it an unchecked exception.
 *
 * @see BaseException
 * @see org.openqa.selenium.WebDriver
 */
@SuppressWarnings("serial") // Indicates that this class doesn't need a custom serialVersionUID
public class BrowserInvocationException extends BaseException {
    /**
     * Constructs a new {@code BrowserInvocationException} with the specified detail message.
     * The cause is not initialized.
     *
     * @param message The detail message which is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public BrowserInvocationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code BrowserInvocationException} with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public BrowserInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
