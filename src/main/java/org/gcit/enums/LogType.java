package org.gcit.enums;

/**
 * The LogType enum represents various levels of logging severity.
 * It is used to categorize the nature of the log messages.
 */
public enum LogType {
    /**
     * Designates events that are very severe errors that will presumably lead the application to abort.
     */
    FATAL,
    /**
     * Designates finer-grained informational events than the DEBUG. Useful for detailed tracing of application behavior.
     */
    TRACE,
    /**
     * Designates potentially harmful situations or warnings that do not prevent the application from continuing.
     */
    WARN,
    /**
     * Designates fine-grained informational events that are most useful to debug an application.
     */
    DEBUG,
    /**
     * Designates error events that might still allow the application to continue running.
     */
    ERROR,
    /**
     * Designates informational messages that highlight the progress of the application at coarse-grained level.
     */
    INFO;
}
