package org.gcit.annotations;

import org.gcit.enums.CategoryType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation to associate metadata such as authors and categories with test methods.
 * <p>
 * This annotation can be applied to test methods (as specified by {@code @Target(METHOD)})
 * and its values can be retrieved at runtime (as specified by {@code @Retention(RUNTIME)}).
 * This information is typically used by test listeners or reporting tools to organize,
 * filter, or provide additional context for test results.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @FrameworkAnnotation(author = {"John Doe", "Jane Smith"}, category = {CategoryType.SMOKE, CategoryType.REGRESSION})
 * @Test
 * public void myTestMethod() {
 *     // ... test logic ...
 * }
 * }
 * </pre>
 *
 * @see org.gcit.enums.CategoryType
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface FrameworkAnnotation {
    /**
     * Specifies the author(s) of the test method.
     * This allows for tracking test case ownership or primary points of contact.
     * Defaults to an empty array if no authors are specified.
     *
     * @return an array of strings representing the authors' names.
     */
    String[] author() default {};

    /**
     * Assigns one or more categories to the test method.
     * This is useful for grouping tests (e.g., SMOKE, REGRESSION, HIGH_LEVEL_TESTING) and can be
     * used by test runners or listeners to selectively execute or report on tests based on these categories.
     * Defaults to an empty array if no categories are specified.
     *
     * @return An array of {@link CategoryType} enums representing the categories associated with the test method.
     */
    CategoryType[] category() default {};

}
