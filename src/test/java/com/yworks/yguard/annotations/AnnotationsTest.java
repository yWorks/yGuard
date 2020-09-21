/*
 * AnnotationsTest.java
 *
 * Created on May 25, 2005, 3:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.yworks.yguard.annotations;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author muellese
 */
public class AnnotationsTest
{
  private static final Logger log = Logger.getLogger(AnnotationsTest.class.getName() );

  /** Creates a new instance of AnnotationsTest */
  @TestAnnotation(id=23, test1="blah", test3=2, classType=AnnotationsTest.class, enumTest = TestEnum.V3, recursive = @YATAnnotation(blah = "gaga"), classArray= {AnnotationsTest.class, String.class}, intArray = {2,3,4})
  public AnnotationsTest()
  {
    try {
      Constructor c = getClass().getConstructor();
      Annotation[] a = c.getAnnotations();
      for (int i = 0; i < a.length; i++){
       log.log(Level.FINE, "annotation " + a[i]);
        if (a[i] instanceof TestAnnotation){
          TestAnnotation ta = (TestAnnotation) a[i];
         log.log(Level.FINE, "id = 23 ? " + ta.id());
         log.log(Level.FINE, "test1 = blah ? " + ta.test1());
         log.log(Level.FINE, "test2 = test ? " + ta.test2());
         log.log(Level.FINE, "test3 = 2 ? " + ta.test3());
         log.log(Level.FINE, "classType " + ta.classType());
         log.log(Level.FINE, "recursive " + ta.recursive());
         log.log(Level.FINE, "enumTest " + ta.enumTest());
         log.log(Level.FINE, "classArray " + ta.classArray()[0]);
        }
      }
    } catch (Exception ex){
      ex.printStackTrace();
    }    
  }
  

  @Test
  public void run() {
    Object o = new AnnotationsTest();
  }
}
