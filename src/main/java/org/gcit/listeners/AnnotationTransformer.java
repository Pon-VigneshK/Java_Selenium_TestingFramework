package org.gcit.listeners;

import org.gcit.utils.DataProviderUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Implements the TestNG {@link org.testng.IAnnotationTransformer} interface to modify
 * TestNG annotations at runtime.
 * <p>
 * This transformer is automatically applied by TestNG (if configured in the test suite XML or via a listener)
 * to every test method. Its primary role in this framework is to:
 * <ol>
 *   <li>Set a default data provider ({@link DataProviderUtils#getTestData(java.lang.reflect.Method)}) for all test methods.
 *       This simplifies test method declarations as they don't need to explicitly specify {@code @DataProvider}.</li>
 *   <li>Set a default retry analyzer ({@link RetryFailedTests}) for all test methods, enabling automatic
 *       retries for failed tests based on the framework's retry logic.</li>
 * </ol>
 *
 * @see org.testng.IAnnotationTransformer
 * @see org.gcit.utils.DataProviderUtils
 * @see RetryFailedTests
 */
public class AnnotationTransformer implements IAnnotationTransformer {
    /**
     * This method is called by TestNG for every test method that is encountered, allowing
     * for dynamic modification of its {@link org.testng.annotations.ITestAnnotation}.
     * <p>
     * It performs the following transformations:
     * <ul>
     *   <li>Sets the data provider name to "getTestData".</li>
     *   <li>Sets the data provider class to {@link DataProviderUtils DataProviderUtils.class}.
     *       This directs TestNG to use the {@code getTestData} method within {@code DataProviderUtils}
     *       to supply data to the test method.</li>
     *   <li>Sets the retry analyzer to {@link RetryFailedTests RetryFailedTests.class}.
     *       This enables the test method to be retried upon failure, according to the logic
     *       defined in {@link RetryFailedTests}.</li>
     * </ul>
     *
     * @param annotation      The {@link org.testng.annotations.ITestAnnotation} instance associated with the test method.
     *                        This annotation is modified directly by this method.
     * @param testClass       The class that contains the test method. This parameter is not used in this implementation.
     * @param testConstructor The constructor of the test class. This parameter is not used in this implementation.
     * @param testMethod      The specific test method ({@link java.lang.reflect.Method}) being transformed.
     *                        This parameter is not directly used to alter logic but is part of the interface.
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setDataProvider("getTestData"); // Sets the name of the data provider method
        annotation.setDataProviderClass(DataProviderUtils.class); // Specifies the class containing the data provider
        annotation.setRetryAnalyzer(RetryFailedTests.class); // Sets the retry analyzer for handling failed tests
    }
}
