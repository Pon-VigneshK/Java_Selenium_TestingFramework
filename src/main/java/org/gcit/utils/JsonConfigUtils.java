package org.gcit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.DataBaseProperties;
import org.gcit.enums.LogType;
import org.gcit.exceptions.JsonExceptions;
import org.gcit.logger.LogService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for handling configuration properties stored in a JSON file.
 * This class reads the JSON configuration file and provides a method to retrieve
 * properties based on provided keys.
 */
public class JsonConfigUtils {
    /**
     * A static map that holds configuration properties read from a JSON file.
     * The keys and values in the map represent configuration settings used throughout the application.
     * This map is initialized when the class is loaded and populated with data from a specified
     * JSON configuration file.
     */
    private static Map<String, String> CONFIGMAP;

    static {
        try {
            CONFIGMAP = new ObjectMapper().readValue(new File(FrameworkConstants.getDBConfigJSONPath()),
                    new TypeReference<HashMap<String, String>>() {
                    });

        } catch (FileNotFoundException e) {
            LogService.log(LogType.ERROR, "Database configuration JSON file not found: " + FrameworkConstants.getDBConfigJSONPath(), e);
            throw new JsonExceptions("Database configuration JSON file not found: " + FrameworkConstants.getDBConfigJSONPath(), e);
        } catch (IOException e) {
            LogService.log(LogType.ERROR, "Error reading database configuration JSON file: " + FrameworkConstants.getDBConfigJSONPath(), e);
            throw new JsonExceptions("Error reading database configuration JSON file: " + FrameworkConstants.getDBConfigJSONPath(), e);
        }
    }

    /**
     * Private constructor to prevent instantiation of the utility class.
     * This class is designed to be used statically.
     */
    private JsonConfigUtils() {
    }

    /**
     * Retrieves the configuration value for the given database property key.
     *
     * @param key The {@link DataBaseProperties} enum constant representing the desired database configuration key.
     *            Must not be null.
     * @return The string value of the database property if found.
     *         Returns {@code null} if the property key is present in the enum but not found in the JSON configuration,
     *         or if the value associated with the key is {@code null}.
     * @throws JsonExceptions if the provided {@code key} is {@code null}, if the key is not found in the JSON configuration map,
     *                        or if an unexpected error occurs during lookup. The original exception causing this will be wrapped.
     * @see DataBaseProperties
     * @see FrameworkConstants#getDBConfigJSONPath()
     */
    public static String get(DataBaseProperties key) throws JsonExceptions {
        if (Objects.isNull(key)) {
            // This case should ideally lead to an exception before map access.
            // The current catch-all would handle it, but explicit is better.
            LogService.log(LogType.ERROR, "Attempted to retrieve database property with a null key.");
            throw new JsonExceptions("Attempted to retrieve database property with a null key. Key cannot be null.");
        }
        try {
            String value = CONFIGMAP.get(key.name().toLowerCase());
            if (Objects.isNull(value)) {
                // Log a warning if a defined DataBaseProperty is not found in the JSON file.
                LogService.log(LogType.WARN, "Database property with key '" + key.name().toLowerCase() + "' not found in " + FrameworkConstants.getDBConfigJSONPath() + ". Returning null.");
            }
            return value;
        } catch (Exception e) {
            // This catch block is for any unexpected issues during lookup from the map,
            // though most common cases (null key, key not found) are handled above or by map behavior.
            LogService.log(LogType.ERROR, "Failed to retrieve database property for key: " + key + " from " + FrameworkConstants.getDBConfigJSONPath(), e);
            throw new JsonExceptions("Property name " + key.name() + " is not found or caused an error during lookup in the JSON file. Please check the DataBaseConfig.json file.", e);
        }
    }
}
