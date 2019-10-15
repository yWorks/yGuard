/*
 * TestMain.java
 *
 * Created on December 3, 2002, 11:04 AM
 */

package com.yworks.yguard.test;

/**
 *
 * @author  muellese
 */
public class TestMain
{
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    Object o = new Object();
    String s = "asdf";
    Object os = args.length == 1 ? s:o;
//    AbstractBase base, base2;
//    base = new ConcreteClass();
//    base2 = new ConcreteSubclass();
//
//    BaseInterface bi = (ConcreteSubclass) base2;
//
//    ExternalInterface ei = base;
//    ei.abstractMethod(s);
//    ei.concreteMethod(s);
//
//    ConcreteSubclass cs = (ConcreteSubclass) bi;

//    bi.abstractMethod(o);
//    bi.abstractMethod(s);
//    bi.concreteMethod(s);
//
//    cs.concreteMethod(o);
//    cs.concreteMethod(s);
//    cs.concreteMethod(os);
//
//    cs.abstractMethod(o);
//    cs.abstractMethod(s);
//    cs.abstractMethod(os);
//
//    base.concreteMethod(o);
//    base.concreteMethod(os);
//    base.concreteMethod(s);
//
//    base2.concreteMethod(o);
//    base2.concreteMethod(os);
//    base2.concreteMethod(s);

    try {
      System.out.println("Class forName success: " + Class.forName("com.yworks.yguard.test.ConcreteClass").getName());
    } catch (Throwable t){
      System.out.println("Error in class forname " + t);
    }

    Thread.dumpStack();
  }
  
}
