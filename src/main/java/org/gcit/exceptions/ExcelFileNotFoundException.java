package org.gcit.exceptions;

public class ExcelFileNotFoundException extends InvalidFilepathException {
    public ExcelFileNotFoundException(String message) {
        super(message);
    }

    public ExcelFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
