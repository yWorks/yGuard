/*
 * TestGenerics.java
 *
 * Created on 22. April 2005, 09:35
 */

package com.yworks.yguard.test.generics;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 *
 * @author muellese
 */
public class TestGenerics {
  
  @Deprecated
  private ParameterizedType<MyStringType> localVar;
  
  /** Creates a new instance of TestGenerics */
  public TestGenerics() {
  }
  
  public void run(){
    new GenericSignatureFormatError();
    ParameterizedType<MyStringType> pt = new ParameterizedType<MyStringType>();
    pt.add(new MyStringType());
    pt.add(new MyStringType(){});
    for (MyType myType : pt.getList()){
      System.out.println();
      System.out.println("myType " + myType);
      System.out.println("Enclosed by " + myType.getClass().getEnclosingMethod());
      System.out.println("Enclosed by " + myType.getClass().getEnclosingClass().getName());
    }
    
//    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : this.getClass().getDeclaredFields()){
      System.out.println();
      for (Annotation a : field.getAnnotations()){
        System.out.println(a);
      }
      System.out.println(field);
      System.out.println("generic type " + field.getGenericType());
    }
    
    for (TypeVariable tv : pt.getClass().getTypeParameters()){
      System.out.println();
      System.out.println(tv);
      for (Type t : tv.getBounds()){
        System.out.println("bounds " + t);
      }
    }
  }
  
  public static class MyStringType extends MyType<String> {
    public MyStringType(){
      super("Hallo!");
    }
    
    public String toString(){
      return super.getContent();
    }
  }
  
  public static void main(String... args){
    new TestGenerics().run();
  }
}
