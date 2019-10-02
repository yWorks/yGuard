/*
 * Generics.java
 *
 * Created on April 20, 2005, 9:04 AM
 */

package com.yworks.yguard.test;

import java.util.List;

/**
 *
 * @author muellese
 */
public class Generics<T extends Object>
{
  
  public enum MyEnum {ASDF, JKL};
  
  /** Creates a new instance of Generics */
  public Generics(Object... a)
  {
    System.out.println(MyEnum.ASDF);
    MyEnum enumValue = MyEnum.ASDF;
    Generics<T> g = this;
    new Object(){
      {
        Object o = null;
        o.toString();
      }
    };

  }

  public static final String CONSTANT = "CONSTANT";

    public static final void main(String[] args){
      new Generics<List<String>>();


    }
}
