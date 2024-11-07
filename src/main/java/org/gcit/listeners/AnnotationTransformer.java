package org.gcit.listeners;

import org.gcit.utils.DataProviderUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The AnnotationTransformer class implements the IAnnotationTransformer interface to modify
 * TestNG annotations at runtime. Specifically, it adjusts data provider settings and retry
 * analyzer for test cases.
 */
public class AnnotationTransformer implements IAnnotationTransformer {
    /**
     * Transforms the given ITestAnnotation instance by setting custom data provider and retry analyzer configurations.
     *
     * @param annotation      the annotation instance to be transformed
     * @param testClass       the class that contains the test method (can be null)
     * @param testConstructor the constructor of the test class (can be null)
     * @param testMethod      the test method (can be null)
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setDataProvider("getTestData");
        annotation.setDataProviderClass(DataProviderUtils.class);
        annotation.setRetryAnalyzer(RetryFailedTests.class);
    }
}
