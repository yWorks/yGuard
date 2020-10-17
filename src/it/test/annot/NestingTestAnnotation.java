package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The interface Nesting test annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PACKAGE, ElementType.TYPE})
public @interface NestingTestAnnotation {

  /**
   * Value test annotation.
   *
   * @return the test annotation
   */
  TestAnnotation value() default @TestAnnotation(value = "NestedTestDefault");

}
