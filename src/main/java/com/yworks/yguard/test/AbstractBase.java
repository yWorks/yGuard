/*
 * AbstractBase.java
 *
 * Created on December 3, 2002, 10:56 AM
 */

package com.yworks.yguard.test;

/**
 *
 * @author  muellese
 */
public abstract class AbstractBase implements ExternalInterface
{
  public static String staticFieldA = "allo";
  public static String staticFieldB = "ballo";
  public static String staticFieldC = "callo";

  /** Creates a new instance of AbstractBase */
  public AbstractBase()
  {
  }


  public abstract void abstractMethod(Object o);

  public abstract void abstractMethod(String o);

  public void concreteMethod(Object o){
    System.out.println("success in AbstractBase.concreteMethod(Object)");
  }

  public void concreteMethod(String o){
    System.out.println("success in AbstractBase.concreteMethod(String)");
  }

}
