/*
 * TestGenerics.java
 *
 * Created on 22. April 2005, 09:35
 */

package com.yworks.yguard.generics;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Test generics.
 *
 * @author muellese
 */
public class TestGenerics {
  private static final Logger log = Logger.getLogger(TestGenerics.class.getName() );

  @Deprecated
  private ParameterizedType<MyStringType> localVar;

    /**
     * Creates a new instance of TestGenerics
     */
    public TestGenerics() {
  }

    /**
     * Run.
     */
    @Test
  public void run(){
    new GenericSignatureFormatError();
    ParameterizedType<MyStringType> pt = new ParameterizedType<MyStringType>();
    pt.add(new MyStringType());
    pt.add(new MyStringType(){});
    for (MyType myType : pt.getList()){
      log.log(Level.FINE, "myType " + myType);
      log.log(Level.FINE, "myType " + myType);
      log.log(Level.FINE, "Enclosed by " + myType.getClass().getEnclosingMethod());
      log.log(Level.FINE, "Enclosed by " + myType.getClass().getEnclosingClass().getName());
    }
    
//    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : this.getClass().getDeclaredFields()){
      for (Annotation a : field.getAnnotations()){
        log.log(Level.FINE, a.toString());
      }
      log.log(Level.FINE, field.toString());
      log.log(Level.FINE, "generic type " + field.getGenericType());
    }
    
    for (TypeVariable tv : pt.getClass().getTypeParameters()){
      log.log(Level.FINE, tv.toString());
      for (Type t : tv.getBounds()){
        log.log(Level.FINE, "bounds " + t);
      }
    }
  }

    /**
     * The type My string type.
     */
    public static class MyStringType extends MyType<String> {
        /**
         * Instantiates a new My string type.
         */
        public MyStringType(){
      super("Hallo!");
    }
    
    public String toString(){
      return super.getContent();
    }
  }

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(String... args){
    new TestGenerics().run();
  }
}
