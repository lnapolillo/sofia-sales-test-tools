package com.despegar.javatemplate.util.web.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomServiceName {
    /**
     * Custom service name, otherwise inferred method name
     */
    String value() default "";
}