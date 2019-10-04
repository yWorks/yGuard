/*
 * B.java
 *
 * Created on December 16, 2002, 11:42 AM
 */

package com.yworks.yguard.test;

import com.yworks.yguard.test.external.ExternalInterface;
import com.yworks.yguard.test.external.ExternalSuperClass;

/**
 *
 * @author  Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public class B extends ExternalSuperClass implements ExternalInterface
{
  
  /** Creates a new instance of B */
  public B()
  {
    System.out.println(A.class);
    System.out.println(A.A1.class);
    System.out.println(B.class);
  }
  
  public void dooFoo(String foo)
  {
    System.out.println("dooing foo for "+foo);
  }
  
  public void havingFun(){
    System.out.println("having really fun!");
  }

  public void havingFun2(){
    System.out.println("having really fun!");
  }
  
  public void dooBar(String bar){
    System.out.println("dooing bar for "+bar);
  }
  
  public double doubleValue()
  {
    return -42.0d;
  }
  
  public float floatValue()
  {
    return 42f;
  }
  
  public int intValue()
  {
    return 4242;
  }
  
  public long longValue()
  {
    return 42424242424242L;
  }
}
