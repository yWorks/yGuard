/*
 * MyType.java
 *
 * Created on 22. April 2005, 09:31
 */

package com.yworks.yguard.generics;

/**
 * The type My type.
 *
 * @param <T> the type parameter
 * @author muellese
 */
public class MyType<T> {
  
  private T t;

  /**
   * Creates a new instance of MyType
   *
   * @param t the t
   */
  public MyType(T t) {
    this.t = t;
  }

  /**
   * Get content t.
   *
   * @return the t
   */
  public T getContent(){
    return t;
  }

  /**
   * Set content.
   *
   * @param t the t
   */
  public void setContent(T t){
    this.t = t;
  }
  
}
