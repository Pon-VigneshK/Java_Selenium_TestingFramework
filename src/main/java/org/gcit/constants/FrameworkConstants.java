package org.gcit.constants;

import java.io.File;

public final class FrameworkConstants {

    private static final String RESOURCES_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    private static final String CHROME_DRIVER_PATH = RESOURCES_PATH + File.separator + "executables" + File.separator + "chromedriver.exe";
    private static final String CONFIG_FILE_PATH = RESOURCES_PATH + File.separator + "config" + File.separator + "config.properties";

    private FrameworkConstants() {
    }

    public static String getChromeDriverPath() {
        return CHROME_DRIVER_PATH;
    }

    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH;
    }
}
