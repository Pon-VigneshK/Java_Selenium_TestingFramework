package org.gcit.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gcit.enums.LogType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * LogService is a utility class for handling different levels of logging messages.
 * The class leverages a Logger to log messages categorized by various LogType levels
 * such as TRACE, DEBUG, INFO, WARN, ERROR, and FATAL.
 * The class operates using a static logger and a map that associates each LogType
 * with its corresponding logging action.
 */
public class LogService {
    /**
     * Logger instance used to log messages for the LogService class.
     * The logger operates at different log levels such as TRACE, DEBUG, INFO, WARN, ERROR, and FATAL.
     * It is retrieved using LogManager to facilitate logging operations in LogService.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * A Consumer that logs TRACE level messages using the preconfigured logger.
     * Formats the log message with a "TRACE: " prefix.
     */
    private static final Consumer<String> TRACE = message -> logger.trace("TRACE: {}", message);
    /**
     * A constant Consumer that logs messages with WARN level using the logger.
     * This variable is utilized to log warning messages formatted with a "WARN: {}" prefix.
     */
    private static final Consumer<String> WARN = message -> logger.warn("WARN: {}", message);
    /**
     * DEBUG is a logging Consumer that logs messages at the DEBUG level.
     * It formats messages with a specific prefix "DEBUG: ".
     * This Consumer uses the logger to log messages intended to provide
     * fine-grained informational events useful in debugging an application.
     */
    private static final Consumer<String> DEBUG = message -> logger.debug("DEBUG: {}", message);
    /**
     * A consumer that logs messages at the ERROR level.
     * This consumer accepts a string message and logs it using the logger's error method.
     */
    private static final Consumer<String> ERROR = message -> logger.error("ERROR: {}", message);
    /**
     * The INFO variable is a Consumer functional interface
     * designed to handle logging of informational messages
     * using the logger instance. It formats the message
     * with the "INFO:" prefix before logging it.
     */
    private static final Consumer<String> INFO = message -> logger.info("INFO: {}", message);
    /**
     * A Consumer functional interface instance to log messages at the FATAL level.
     * It prefixes the logged message with "FATAL: " and uses the logger's fatal method
     * to record messages indicating critical system errors that may lead to an application
     * shutdown.
     */
    private static final Consumer<String> FATAL = message -> logger.fatal("FATAL: {}", message);
    /**
     * A map that associates each LogType with its corresponding logging action.
     * This map is statically initialized to map each LogType to an appropriate
     * Consumer<String> that defines how messages of that log type should be handled.
     */
    private static final Map<LogType, Consumer<String>> LOGSMAP = new EnumMap<>(LogType.class);

    static {
        LOGSMAP.put(LogType.TRACE, TRACE);
        LOGSMAP.put(LogType.WARN, WARN);
        LOGSMAP.put(LogType.DEBUG, DEBUG);
        LOGSMAP.put(LogType.ERROR, ERROR);
        LOGSMAP.put(LogType.INFO, INFO);
        LOGSMAP.put(LogType.FATAL, FATAL);
    }

    /**
     * Private constructor to prevent instantiation of the LogService class.
     * This enforces the utility nature of the class, ensuring that it operates only with static methods.
     */
    private LogService() {
    }

    /**
     * Logs a message using the specified log type.
     *
     * @param logType the type of log to be used for the given message. Possible values are TRACE, DEBUG, INFO, WARN, ERROR, and FATAL.
     * @param message the message to be logged.
     */
    public static void log(LogType logType, String message) {
        Consumer<String> consumer = LOGSMAP.get(logType);
        if (consumer != null) {
            consumer.accept(message);
        } else {
            logger.warn("Unsupported log type: " + logType);
        }
    }
}
