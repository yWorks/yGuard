/*
 * ParameterizedType.java
 *
 * Created on 22. April 2005, 09:30
 */

package com.yworks.yguard.test.generics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author muellese
 */
public class ParameterizedType<T extends MyType<String>> {
  
  private ArrayList<T> list = new ArrayList<T>();
  
  /** Creates a new instance of ParameterizedType */
  public ParameterizedType() {
  }
  
  public List<T> getList(){
    return list;
  }

  public void add(T t){
    this.list.add(t);
    System.out.println(t.getClass());
  }
}
