/*
 * ParameterizedType.java
 *
 * Created on 22. April 2005, 09:30
 */

package com.yworks.yguard.generics;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Parameterized type.
 *
 * @param <T> the type parameter
 * @author muellese
 */
public class ParameterizedType<T extends MyType<String>> {
  
  private ArrayList<T> list = new ArrayList<T>();

  /**
   * Creates a new instance of ParameterizedType
   */
  public ParameterizedType() {
  }

  /**
   * Get list list.
   *
   * @return the list
   */
  public List<T> getList(){
    return list;
  }

  /**
   * Add.
   *
   * @param t the t
   */
  public void add(T t){
    this.list.add(t);
    System.out.println(t.getClass());
  }
}
