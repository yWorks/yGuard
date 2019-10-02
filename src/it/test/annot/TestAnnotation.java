package test.annot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.PACKAGE,ElementType.TYPE})
public @interface TestAnnotation {

  String value() default "DefaultString";
  String unusedValue() default "FindMe!";
}
