/*
 * ConcreteClass.java
 *
 * Created on December 3, 2002, 10:58 AM
 */

package com.yworks.yguard.test;

/**
 *
 * @author  muellese
 */
public class ConcreteClass extends AbstractBase
{
  private String staticFieldB = "gallo";
  private static String staticFieldC = "hallo";
  
  /** Creates a new instance of ConcreteClass */
  public ConcreteClass()
  {
    System.out.println(staticFieldA);
    System.out.println(AbstractBase.staticFieldA);
    System.out.println(ConcreteClass.staticFieldA);
    System.out.println(staticFieldB);
    System.out.println(AbstractBase.staticFieldB);
    System.out.println(this.staticFieldB);
    System.out.println(staticFieldC);
    System.out.println(AbstractBase.staticFieldC);
    System.out.println(ConcreteClass.staticFieldC);
  }
  
  public static void main(String[] args){
    new ConcreteClass();
  }
  
  public void abstractMethod(String o)
  {
     System.out.println("success in ConcreteCass.abstractMethod(String)");
     new InnerClass().concreteMethod("Huibu");
  }

  /**
   * @deprecated
   */
  public void abstractMethod(Object o)
  {
     System.out.println("deprecated method success in ConcreteCass.abstractMethod(Object)");
  }

  private final class InnerClass implements BaseInterface {

    public void abstractMethod(Object o)
    {
     System.out.println("success in ConcreteCass.abstractMethod(String)");
    }

    public void concreteMethod(String s)
    {
     System.out.println("this is "+InnerClass.this);
     System.out.println("aka which is of type "+InnerClass.class);
     System.out.println("outer is "+ConcreteClass.this);
     System.out.println("which is of type "+ConcreteClass.class);
     System.out.println("success in InnerClass.concreteMethod(String)");
    }
  }
  
}
