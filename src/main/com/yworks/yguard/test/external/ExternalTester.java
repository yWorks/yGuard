/*
 * ExternalTester.java
 *
 * Created on May 22, 2003, 10:31 AM
 */

package com.yworks.yguard.test.external;

import com.yworks.yguard.test.B;

/**
 *
 * @author  muellese
 */
public class ExternalTester
{
  
  /** Creates a new instance of ExternalTester */
  public ExternalTester()
  {
  }
  
  public static void main(String[] args){
    B b = new B();
    Number number = (Number) b;
    ExternalInterface ei = (ExternalInterface) b;
    ExternalSuperClass esc = (ExternalSuperClass) b;
    System.out.println("number "+number.floatValue());
    ei.dooFoo("foooooo");
    esc.havingFun2();
    
    System.out.println(b.floatValue());
    b.havingFun();
    b.dooFoo("foo");
  }
  
}
