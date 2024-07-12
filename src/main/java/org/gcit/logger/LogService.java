package org.gcit.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gcit.enums.LogType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class LogService {
    private static final Logger logger = LogManager.getLogger();
    private static final Consumer<String> TRACE = message -> logger.trace("TRACE: {}", message);
    private static final Consumer<String> WARN = message -> logger.warn("WARN: {}", message);
    private static final Consumer<String> DEBUG = message -> logger.debug("DEBUG: {}", message);
    private static final Consumer<String> ERROR = message -> logger.error("ERROR: {}", message);
    private static final Consumer<String> INFO = message -> logger.info("INFO: {}", message);
    private static final Consumer<String> FATAL = message -> logger.fatal("FATAL: {}", message);
    private static final Map<LogType, Consumer<String>> LOGSMAP = new EnumMap<>(LogType.class);

    static {
        LOGSMAP.put(LogType.TRACE, TRACE);
        LOGSMAP.put(LogType.WARN, WARN);
        LOGSMAP.put(LogType.DEBUG, DEBUG);
        LOGSMAP.put(LogType.ERROR, ERROR);
        LOGSMAP.put(LogType.INFO, INFO);
        LOGSMAP.put(LogType.FATAL, FATAL);
    }

    private LogService() {
    }

    public static void log(LogType logType, String message) {
        Consumer<String> consumer = LOGSMAP.get(logType);
        if (consumer != null) {
            consumer.accept(message);
        } else {
            logger.warn("Unsupported log type: " + logType);
        }
    }
}
