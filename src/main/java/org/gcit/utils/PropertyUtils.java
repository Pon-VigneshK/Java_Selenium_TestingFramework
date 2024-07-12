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

public class PropertyUtils {

    private final static Map<String, String> CONFIG_MAP = new HashMap<>();
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

    private PropertyUtils() {
        // Private constructor to prevent instantiation of the class.
    }

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