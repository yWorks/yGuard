/*
 * A.java
 *
 * Created on December 13, 2002, 1:37 PM
 */

package com.yworks.yguard.test;

/**
 *
 * @author  muellese
 */
public class A
{
  private static final String PRIVATE_STATIC_FINAL = "TEST";
  static final String STATIC_FINAL = "TEST";
  public static final String PUBLIC_STATIC_FINAL = "TEST";

  private final String PRIVATE_FINAL = "TEST";
  final String FINAL = "TEST";
  public final String PUBLIC_FINAL = "TEST";
  
  private static String PRIVATE_STATIC = "TEST";
  static String STATIC = "TEST";
  public static String PUBLIC_STATIC = "TEST";

  Class A;
  
  /** Creates a new instance of A */
  public A() throws Exception
  {
    System.out.println("com.yworks.yguard.test.A");
    this.A = A1.class;
    System.out.println(this.A);
    this.A = A.class;
    System.out.println(this.A);
    System.out.println(B.class);
    this.A = Class.forName("com.yworks.yguard.test.BaseInterface");
    this.A = Class.forName("com.yworks.yguard.test.A$A1");
    this.A = Class.forName("com.yworks.yguard.test.A$A1", true, this.getClass().getClassLoader());
    System.out.println(this.A);
    System.out.println(PUBLIC_STATIC);

    A[] a = new A[3];
    a.clone();
    a.toString();
    System.out.println("a.length" + a.length);
  }
  
  public static void main(String[] args) throws Exception{
    new A();
  }
  
  public static final class A1 extends Object{
    
  }
  
}
