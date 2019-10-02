/*
 * MyType.java
 *
 * Created on 22. April 2005, 09:31
 */

package com.yworks.yguard.test.generics;

/**
 *
 * @author muellese
 */
public class MyType<T> {
  
  private T t;
  
  /** Creates a new instance of MyType */
  public MyType(T t) {
    this.t = t;
  }
  
  public T getContent(){
    return t;
  }
  
  public void setContent(T t){
    this.t = t;
  }
  
}
