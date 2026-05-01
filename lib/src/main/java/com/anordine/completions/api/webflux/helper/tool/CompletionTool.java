package com.anordine.completions.api.webflux.helper.tool;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CompletionTool {

    String name() default "";

    String description() default "";

    boolean strict() default true;
}
