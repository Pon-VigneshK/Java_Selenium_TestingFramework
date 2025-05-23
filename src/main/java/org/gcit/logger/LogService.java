package org.gcit.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gcit.enums.LogType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Centralized utility class for logging messages throughout the framework.
 * <p>
 * This service uses Apache Log4j 2 as the underlying logging engine. It provides a simplified
 * interface for logging by mapping a custom {@link LogType} enum to corresponding
 * Log4j logging levels (TRACE, DEBUG, INFO, WARN, ERROR, FATAL).
 * <p>
 * Messages logged via this service are prefixed with their {@link LogType} (e.g., "INFO: Login successful").
 * The actual logging behavior (e.g., output format, appenders like console or file)
 * is configured in the Log4j 2 configuration file (typically {@code log4j2.xml} or {@code log4j2.properties})
 * in the classpath.
 * <p>
 * This class is final and cannot be instantiated; all methods are static.
 *
 * @see LogType
 * @see org.apache.logging.log4j.Logger
 * @see org.apache.logging.log4j.LogManager
 */
public class LogService {
    /**
     * The underlying Apache Log4j 2 Logger instance used for all logging operations in this service.
     * It is obtained once using {@link LogManager#getLogger()} for this class.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * A {@link Consumer} that logs messages at the TRACE level via Log4j.
     * Messages are prefixed with "TRACE: ".
     */
    private static final Consumer<String> TRACE = message -> logger.trace("TRACE: {}", message);

    /**
     * A {@link Consumer} that logs messages at the WARN level via Log4j.
     * Messages are prefixed with "WARN: ".
     */
    private static final Consumer<String> WARN = message -> logger.warn("WARN: {}", message);

    /**
     * A {@link Consumer} that logs messages at the DEBUG level via Log4j.
     * Messages are prefixed with "DEBUG: ".
     */
    private static final Consumer<String> DEBUG = message -> logger.debug("DEBUG: {}", message);

    /**
     * A {@link Consumer} that logs messages at the ERROR level via Log4j.
     * Messages are prefixed with "ERROR: ".
     * This consumer is also used by the overloaded {@link #log(LogType, String, Throwable)} method
     * when logging exceptions at the ERROR level.
     */
    private static final Consumer<String> ERROR = message -> logger.error("ERROR: {}", message);

    /**
     * A {@link Consumer} that logs messages at the INFO level via Log4j.
     * Messages are prefixed with "INFO: ".
     */
    private static final Consumer<String> INFO = message -> logger.info("INFO: {}", message);

    /**
     * A {@link Consumer} that logs messages at the FATAL level via Log4j.
     * Messages are prefixed with "FATAL: ".
     */
    private static final Consumer<String> FATAL = message -> logger.fatal("FATAL: {}", message);

    /**
     * A static {@link EnumMap} that maps each {@link LogType} enum constant to its corresponding
     * logging action (a {@link Consumer<String>}). This map is initialized in a static block
     * and is used by the {@link #log(LogType, String)} and {@link #log(LogType, String, Throwable)}
     * methods to perform the appropriate logging operation.
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
     * Logs a message with the specified {@link LogType} and detail message.
     * <p>
     * This method retrieves the appropriate logging action (a {@link Consumer<String>}) from the
     * {@link #LOGSMAP} based on the provided {@code logType}. It then invokes this consumer
     * with the given {@code message}.
     * <p>
     * If the {@code logType} is not found in the map (which should not happen if {@link LogType}
     * is correctly populated in {@link #LOGSMAP}), a warning message is logged via Log4j indicating
     * an unsupported log type.
     *
     * @param logType The {@link LogType} enum value specifying the severity or category of the log message
     *                (e.g., {@link LogType#INFO}, {@link LogType#ERROR}).
     * @param message The message string to be logged.
     */
    public static void log(LogType logType, String message) {
        Consumer<String> consumer = LOGSMAP.get(logType);
        if (consumer != null) {
            consumer.accept(message);
        } else {
            // This case should ideally not occur if LOGSMAP is always in sync with LogType enum
            logger.warn("Unsupported log type provided: " + logType + ". Message: " + message);
        }
    }

    /**
     * Logs a message with the specified {@link LogType}, detail message, and an associated {@link Throwable}.
     * <p>
     * This method is primarily intended for logging error conditions that include an exception.
     * It constructs a combined message string including the original message and the throwable's message.
     * Then, it uses the underlying Log4j logger to log this combined message along with the full stack trace
     * of the throwable, at the level corresponding to the given {@code logType}.
     * <p>
     * For example, if {@code logType} is {@link LogType#ERROR}, the message and stack trace will be logged
     * using {@code logger.error(combinedMessage, throwable)}.
     *
     * @param logType   The {@link LogType} enum value (e.g., {@link LogType#ERROR}, {@link LogType#FATAL}).
     * @param message   The main message string to be logged.
     * @param throwable The {@link Throwable} (e.g., an exception) to be logged, including its stack trace.
     */
    public static void log(LogType logType, String message, Throwable throwable) {
        String fullMessage = message + (throwable == null ? "" : " | Exception: " + throwable.getMessage());
        switch (logType) {
            case TRACE:
                logger.trace(fullMessage, throwable);
                break;
            case DEBUG:
                logger.debug(fullMessage, throwable);
                break;
            case INFO:
                logger.info(fullMessage, throwable);
                break;
            case WARN:
                logger.warn(fullMessage, throwable);
                break;
            case ERROR:
                logger.error(fullMessage, throwable);
                break;
            case FATAL:
                logger.fatal(fullMessage, throwable);
                break;
            default:
                logger.warn("Unsupported log type provided: " + logType + " for message with throwable: " + fullMessage, throwable);
                break;
        }
    }
}
