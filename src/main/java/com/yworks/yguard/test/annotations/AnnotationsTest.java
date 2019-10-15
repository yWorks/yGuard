/*
 * AnnotationsTest.java
 *
 * Created on May 25, 2005, 3:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.yworks.yguard.test.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 *
 * @author muellese
 */
public class AnnotationsTest
{
  
  /** Creates a new instance of AnnotationsTest */
  @TestAnnotation(id=23, test1="blah", test3=2, classType=AnnotationsTest.class, enumTest = TestEnum.V3, recursive = @YATAnnotation(blah = "gaga"), classArray= {AnnotationsTest.class, String.class}, intArray = {2,3,4})
  public AnnotationsTest()
  {
    try {
      Constructor c = getClass().getConstructor();
      Annotation[] a = c.getAnnotations();
      for (int i = 0; i < a.length; i++){
        System.out.println("annotation " + a[i]);
        if (a[i] instanceof TestAnnotation){
          TestAnnotation ta = (TestAnnotation) a[i];
          System.out.println("id = 23 ? " + ta.id());
          System.out.println("test1 = blah ? " + ta.test1());
          System.out.println("test2 = test ? " + ta.test2());
          System.out.println("test3 = 2 ? " + ta.test3());
          System.out.println("classType " + ta.classType());
          System.out.println("recursive " + ta.recursive());
          System.out.println("enumTest " + ta.enumTest());
          System.out.println("classArray " + ta.classArray()[0]);
        }
      }
    } catch (Exception ex){
      ex.printStackTrace();
    }    
  }
  
  
  
  public static void main(String[] args){
    Object o = new AnnotationsTest();
  }
  
}
