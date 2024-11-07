package org.gcit.annotations;

import org.gcit.enums.CategoryType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface FrameworkAnnotation {
    /**
     * Returns the list of authors associated with the annotated method.
     *
     * @return an array of strings representing the authors' names.
     */
    String[] author() default {};

    /**
     * Associates categories with the annotated method for better organization and filtering of tests.
     *
     * @return an array of CategoryType enums representing the categories of the method.
     */
    CategoryType[] category() default {};

}
