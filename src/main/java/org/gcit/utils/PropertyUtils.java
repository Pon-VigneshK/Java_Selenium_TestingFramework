package org.gcit.utils;

import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.ConfigProperties;
import org.gcit.enums.LogType;
import org.gcit.exceptions.PropertiesFileException;
import org.gcit.logger.LogService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Utility class to manage and access properties from a configuration file.
 * <p>
 * This class reads the property values only once when it is first loaded and
 * stores them in a static map. It provides a method to retrieve property
 * values based on keys of type {@link ConfigProperties}.
 */
public class PropertyUtils {

    /**
     * A static map that holds configuration properties loaded from a file.
     * <p>
     * This map is populated when the class is first loaded and contains key-value pairs
     * where both keys and values are strings. Keys are typically the names of configuration
     * properties, and values are their corresponding settings. This allows for quick
     * retrieval of configuration data within the application.
     */
    private final static Map<String, String> CONFIG_MAP = new HashMap<>();
    /**
     * A Properties object used to read and store configuration property values from a file.
     * This object is populated once when the class is first loaded, during the static
     * initializer block. The properties are then transferred into a static map for easy access.
     */
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream file = new FileInputStream(FrameworkConstants.getConfigFilePath())) {
            properties.load(file);
            for (String key : properties.stringPropertyNames()) {
                CONFIG_MAP.put(String.valueOf(key), String.valueOf(properties.getProperty(key)).trim());
            }
            LogService.log(LogType.INFO, "Successfully loaded configuration properties from file: " + FrameworkConstants.getConfigFilePath());
        } catch (IOException e) {
            LogService.log(LogType.ERROR, "Error loading configuration properties");
            throw new RuntimeException("Error loading configuration properties", e);
        }
    }

    /**
     * Private constructor to prevent instantiation of the PropertyUtils class.
     *
     * This class is designed as a utility class with static methods and
     * a static initializer block to read and store the configuration properties.
     */
    private PropertyUtils() {
        // Private constructor to prevent instantiation of the class.
    }

    /**
     * Retrieves the value associated with the given configuration property key from the loaded
     * {@code config.properties} file.
     * <p>
     * The method looks up the property value using the lowercase name of the provided {@link ConfigProperties} enum constant.
     * For example, if {@code ConfigProperties.BROWSER} is passed, it will look for a property named "browser".
     *
     * @param key The {@link ConfigProperties} enum constant representing the desired property key.
     *            Must not be null.
     * @return The trimmed string value of the property if found.
     *         Returns {@code null} if the property key is present in the enum but not found in the properties file,
     *         or if the value associated with the key is {@code null}.
     * @throws PropertiesFileException if the provided {@code key} is {@code null} or if an unexpected error occurs during lookup,
     *                                 though typical missing keys would return {@code null} based on map behavior.
     *                                 The exception is primarily to indicate a failure in finding the key if {@code Objects.isNull(key)} check was not there and map threw error.
     * @see ConfigProperties
     * @see FrameworkConstants#getConfigFilePath()
     */
    public static String getValue(ConfigProperties key) {
        if (Objects.isNull(key)) {
            // Throw an exception or log an error if the key is null, as it's an invalid usage.
            // For now, PropertiesFileException is thrown by the catch block if map access with null key happens.
            // Consider adding an explicit null check for 'key' here if stricter handling is needed.
            // LogService.log(LogType.WARN, "Attempted to retrieve property with a null key.");
        }
        // The existing try-catch will handle potential issues if key.name() itself causes a problem,
        // or if CONFIG_MAP access leads to an unexpected state, though get() on HashMap with a null key is allowed.
        // The main purpose of the try-catch here seems to be to wrap any unexpected issue into PropertiesFileException.
        try {
            String value = CONFIG_MAP.get(key.name().toLowerCase());
            if (Objects.isNull(value)) {
                // Log a warning if a defined ConfigProperty is not found in the file, as it might indicate a missing configuration.
                LogService.log(LogType.WARN, "Property with key '" + key.name().toLowerCase() + "' not found in configuration file. Returning null.");
            }
            return value;
        } catch (Exception e) {
            // This catch block is more of a safeguard for unexpected issues during the lookup.
            LogService.log(LogType.ERROR, "Failed to retrieve property for key: " + key + " due to an unexpected error.", e);
            throw new PropertiesFileException("Property name " + key + " caused an error during lookup. Please check the property file or logs.", e);
        }
    }
}