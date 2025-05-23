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
    /**
     * Waits for the element to be visible and enabled, so it can be clicked.
     */
    CLICKABLE,
    /**
     * Waits for the element to be present in the Document Object Model (DOM).
     * The element does not necessarily need to be visible.
     */
    PRESENCE,
    /**
     * Waits for the element to be present in the DOM and visible on the page.
     * Visibility means the element is displayed and has a height and width greater than 0.
     */
    VISIBLE,
    /**
     * No explicit wait strategy is applied. Assumes the element is immediately available.
     * Use with caution, as this can lead to flaky tests if elements are not ready.
     */
    NONE
}
