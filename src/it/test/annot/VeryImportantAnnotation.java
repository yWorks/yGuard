package test.annot;

import java.lang.annotation.*;

/**
 * The interface Very important annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface VeryImportantAnnotation {

}
