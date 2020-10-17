package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The interface Request for enhancement.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestForEnhancement {
  /**
   * Id int.
   *
   * @return the int
   */
  int id();

  /**
   * Synopsis string.
   *
   * @return the string
   */
  String synopsis();

  /**
   * Engineer string.
   *
   * @return the string
   */
  String engineer() default "[unassigned]";

  /**
   * Date string.
   *
   * @return the string
   */
  String date() default "[unimplemented]";
}