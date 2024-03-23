package org.gcit.utils;

public final class DynamicXpathUtils {
    private DynamicXpathUtils() {
    }

    /**
     * Sample DynamicXpath:
     * String xpath = "//a[text()='%s']" ;   --> In Java, %s is a format specifier used in the String.format() method and
     * other similar methods to represent a placeholder for a string.
     * When we use %s in a format string, it indicates that a string value should be inserted at that position.
     */

    public static String getDynamicXPath(String xpath, String parameter) {
        return String.format(xpath, parameter);
    }

    public static String getDynamicXPath(String parameter, String parameter1, String parameter2) {
        return String.format(parameter, parameter1, parameter2);
    }

    public static String getDynamicXPath(String parameter, String parameter1, String parameter2, String parameter3) {
        return String.format(parameter, parameter1, parameter2, parameter3);
    }

}
