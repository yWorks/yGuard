package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The interface Test inv parameter annotation.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface TestInvParameterAnnotation {

  /**
   * Value string.
   *
   * @return the string
   */
  String value() default "DefaultInvisibleParameterAnnotString";

}
