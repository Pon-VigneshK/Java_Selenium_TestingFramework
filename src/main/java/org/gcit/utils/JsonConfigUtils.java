package org.gcit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gcit.constants.FrameworkConstants;
import org.gcit.enums.DataBaseProperties;
import org.gcit.exceptions.JsonExceptions;

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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
     * @param key the database property key of type DataBaseProperties whose corresponding value is to be fetched.
     * @return the configuration value associated with the given key from the configuration map.
     * @throws Exception if the key is not found in the JSON configuration map.
     */
    public static String get(DataBaseProperties key) throws Exception {
        try {
            if (Objects.isNull(key) || Objects.isNull(CONFIGMAP.get(key.name().toLowerCase()))) {
            }
            return CONFIGMAP.get(key.name().toLowerCase());
        } catch (Exception e) {
            throw new JsonExceptions("Property name " + key + " is not found in the JSON file. Please check the DataBaseConfig.json file.");
        }

    }
}
