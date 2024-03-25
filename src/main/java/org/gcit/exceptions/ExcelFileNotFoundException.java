package org.gcit.exceptions;

public class ExcelFileNotFoundException extends BaseException{
    public ExcelFileNotFoundException(String message) {
        super(message);
    }
    public ExcelFileNotFoundException(String message, Throwable cause) {
        super(message,cause);
    }
}
