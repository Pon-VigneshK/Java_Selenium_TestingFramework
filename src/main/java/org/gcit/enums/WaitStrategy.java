package org.gcit.enums;

/**
 * Enumeration representing different wait strategies for locating web elements
 * within a certain period using the explicit wait mechanism in a Selenium
 * automated test environment.
 *
 * CLICKABLE - Waits until the element is clickable.
 * PRESENCE - Waits until the element is present in the DOM.
 * VISIBLE - Waits until the element is visible on the page.
 * NONE - Does not wait and attempts to find the element immediately.
 */
public enum WaitStrategy {
    CLICKABLE,
    PRESENCE,
    VISIBLE,
    NONE
}
