package org.gcit.listeners;

import org.gcit.utils.DataProviderUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // ----> For Excel Data <----//
//        annotation.setDataProvider("getExcelData");
//        annotation.setDataProviderClass(DataProviderUtils.class);
//        annotation.setRetryAnalyzer(RetryFailedTests.class);

        annotation.setDataProvider("getJsonTestData");
        annotation.setDataProviderClass(DataProviderUtils.class);
        annotation.setRetryAnalyzer(RetryFailedTests.class);
    }
}
