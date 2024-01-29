package com.MobileFolk.Common;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        //  annotation.setRetryAnalyzer(Retry.class);
//        CustomAttribute customAttribute = new CustomAttribute();
//        annotation.setAttributes();
    }

    @Override
    public void transform(IConfigurationAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setTimeOut(7 * 60 * 1000); //7mins
    }

}
