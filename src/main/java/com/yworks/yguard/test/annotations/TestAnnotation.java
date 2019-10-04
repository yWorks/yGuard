/*
 * TestAnnotation.java
 *
 * Created on May 25, 2005, 3:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.yworks.yguard.test.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author muellese
 */
@Target(value = ElementType.CONSTRUCTOR)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TestAnnotation
{
  int    id();
  String test1();
  String test2() default "test"; 
  byte   test3() default (byte) 3; 
  Class  classType() default String.class;
  int[]  intArray() default {1,2,3,4};
  Class[] classArray() default {String.class, Object.class, int.class};
  YATAnnotation recursive() default @YATAnnotation(blah = "blub");
  TestEnum enumTest() default TestEnum.V2;
}
