/*
 * YATAnnotation.java
 *
 * Created on May 30, 2005, 9:51 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.yworks.yguard.test.annotations;

/**
 *
 * @author muellese
 */
public @interface YATAnnotation
{
  String blah() default "blub";
}
