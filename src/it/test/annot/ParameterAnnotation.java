package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The interface Parameter annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParameterAnnotation {

  /**
   * Param annot foo string.
   *
   * @return the string
   */
  String paramAnnotFoo();

  /**
   * Param annot baz string.
   *
   * @return the string
   */
  String paramAnnotBaz() default "[param annot default]";

}
