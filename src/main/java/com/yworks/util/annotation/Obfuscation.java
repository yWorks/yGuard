package com.yworks.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Obfuscation {

  boolean applyToMembers() default true;
  boolean exclude() default true;
}
