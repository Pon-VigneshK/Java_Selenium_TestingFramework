package org.gcit.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.gcit.constants.FrameworkConstants;

public class ReadPropertyFile {

    private static final Properties properties = new Properties();
    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    static {
        try (FileInputStream file = new FileInputStream(FrameworkConstants.getConfigFilePath())) {
            properties.load(file);
            for (String key : properties.stringPropertyNames()) {
                CONFIG_MAP.put(String.valueOf(key), String.valueOf(properties.getProperty(key)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ReadPropertyFile() {
        // Private constructor to prevent instantiation of the class.
    }

    public static String getValue(String key) throws Exception {
        if (Objects.isNull(properties.getProperty(key)) || Objects.isNull(CONFIG_MAP.get(key))) {
            throw new Exception("The property with the name " + key + " is not found. Please check the config.properties file");
        }
        return CONFIG_MAP.get(key);
    }
}
