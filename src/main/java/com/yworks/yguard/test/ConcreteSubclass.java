/*
 * ConcreteSubclass.java
 *
 * Created on December 3, 2002, 11:03 AM
 */

package com.yworks.yguard.test;

/**
 *
 * @author  muellese
 */
public class ConcreteSubclass extends ConcreteClass implements BaseInterface
{
  
  public String staticFieldB = "fallo";
  public static String staticFieldC = "nallo";

  /** Creates a new instance of ConcreteSubclass */
  public ConcreteSubclass()
  {
    System.out.println(staticFieldA);
    System.out.println(AbstractBase.staticFieldA);
    System.out.println(ConcreteClass.staticFieldA);
    System.out.println(staticFieldB);
    System.out.println(AbstractBase.staticFieldB);
    System.out.println(this.staticFieldB);
    System.out.println(staticFieldC);
    System.out.println(AbstractBase.staticFieldC);
    System.out.println(ConcreteSubclass.staticFieldC);
  }

  public static void main(String[] args){
    new ConcreteSubclass();
  }


  public void abstractMethod(Object o) {
    System.out.println("AbstractMethod in ConcreteSubclass : " + getClass().getName());
  }

  public void concreteMethod(String s){
    System.out.println("success in concreteSubCass.concreteMethod(String)");
  }
}
