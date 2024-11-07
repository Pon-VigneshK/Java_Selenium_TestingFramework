package org.gcit.enums;

/**
 * The {@code CategoryType} enum represents different categories of tests
 * that can be assigned to test cases for better organization and filtering.
 *
 * <ul>
 *   <li>HIGH_LEVEL_TESTING: Indicates tests aimed at validating high-level functionality.</li>
 *   <li>SMOKE: Refers to a subset of tests that cover the basic functionalities to ensure viability.</li>
 *   <li>FULL_REGRESSION: Represents a complete suite of tests to ensure that recent changes have not affected existing functionalities.</li>
 * </ul>
 */
public enum CategoryType {
    HIGH_LEVEL_TESTING,
    SMOKE,
    FULL_REGRESSION,

}
