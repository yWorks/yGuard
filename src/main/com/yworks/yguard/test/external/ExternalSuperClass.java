/*
 * ExternalSuperClass.java
 *
 * Created on May 22, 2003, 10:29 AM
 */

package com.yworks.yguard.test.external;

/**
 *
 * @author  muellese
 */
public abstract class ExternalSuperClass extends java.lang.Number
{
  
  /** Creates a new instance of ExternalSuperClass */
  public ExternalSuperClass()
  {
  }
  
  public void havingFun2(){
    System.out.println("this "+doubleValue());
  }
  
}
