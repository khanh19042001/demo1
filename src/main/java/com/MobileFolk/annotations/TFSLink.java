package com.MobileFolk.annotations;

import io.qameta.allure.LinkAnnotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LinkAnnotation(
        type = "tms"
)

public @interface TFSLink {
    String value();
}