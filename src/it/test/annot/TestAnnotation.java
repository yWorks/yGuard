package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The interface Test annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.PACKAGE,ElementType.TYPE})
public @interface TestAnnotation {

    /**
     * Value string.
     *
     *
		 * @return the string
     */
    String value() default "DefaultString";

    /**
     * Unused value string.
     *
     *
		 * @return the string
     */
    String unusedValue() default "FindMe!";
}
