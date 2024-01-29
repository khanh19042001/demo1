package com.MobileFolk.annotations;


import com.MobileFolk.constants.AuthorType;
import com.MobileFolk.constants.CategoryType;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//A Custom Annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Annotation {
    public CategoryType[] category();
    public AuthorType[] author();
    @Nullable
    public AuthorType[] reviewer();
}
