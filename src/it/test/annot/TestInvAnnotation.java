package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The interface Test inv annotation.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface TestInvAnnotation {

    /**
     * Inv value string.
     *
     * @return the string
     */
    String invValue() default "DefaultInvisibleString";

}
