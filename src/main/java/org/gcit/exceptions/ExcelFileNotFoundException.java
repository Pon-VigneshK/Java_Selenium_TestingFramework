package org.gcit.exceptions;

/**
 * ExcelFileNotFoundException is a custom exception that is thrown when an Excel file is not found at the specified filepath.
 * This class extends the InvalidFilepathException and inherits its properties and methods.
 *
 * The exception provides two constructors:
 *
 * - ExcelFileNotFoundException(String message): Constructs a new exception with the specified detail message.
 * - ExcelFileNotFoundException(String message, Throwable cause): Constructs a new exception with the specified detail message and cause.
 */
@SuppressWarnings("serial")
public class ExcelFileNotFoundException extends InvalidFilepathException {
    public ExcelFileNotFoundException(String message) {
        super(message);
    }

    public ExcelFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
