/*
 * TestAnnotation.java
 *
 * Created on May 25, 2005, 3:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.yworks.yguard.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Test annotation.
 *
 * @author muellese
 */
@Target(value = ElementType.CONSTRUCTOR)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TestAnnotation {
  /**
   * Id int.
   *
   * @return the int
   */
  int id();

  /**
   * Test 1 string.
   *
   * @return the string
   */
  String test1();

  /**
   * Test 2 string.
   *
   * @return the string
   */
  String test2() default "test";

  /**
   * Test 3 byte.
   *
   * @return the byte
   */
  byte test3() default (byte) 3;

  /**
   * Class type class.
   *
   * @return the class
   */
  Class classType() default String.class;

  /**
   * Int array int [ ].
   *
   * @return the int [ ]
   */
  int[] intArray() default {1, 2, 3, 4};

  /**
   * Class array class [ ].
   *
   * @return the class [ ]
   */
  Class[] classArray() default {String.class, Object.class, int.class};

  /**
   * Recursive yat annotation.
   *
   * @return the yat annotation
   */
  YATAnnotation recursive() default @YATAnnotation(blah = "blub");

  /**
   * Enum test test enum.
   *
   * @return the test enum
   */
  TestEnum enumTest() default TestEnum.V2;
}
