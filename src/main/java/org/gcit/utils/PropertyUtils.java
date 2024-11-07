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
     * Retrieves the value associated with the given configuration property key.
     *
     * @param key the {@link ConfigProperties} key for which the value is to be fetched
     * @return the value associated with the specified key, or null if the key is not found
     * @throws PropertiesFileException if the specified key is not found in the configuration properties
     */
    public static String getValue(ConfigProperties key) {
        try {
            if (Objects.isNull(key) || Objects.isNull(CONFIG_MAP.get(key.name().toLowerCase()))) {

            }
            return CONFIG_MAP.get(key.name().toLowerCase());
        } catch (Exception e) {
            throw new PropertiesFileException("Property name " + key + " is not found. Please check the property file");
        }

    }
}