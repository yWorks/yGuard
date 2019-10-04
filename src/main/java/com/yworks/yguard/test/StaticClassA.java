/*
 * StaticClassA.java
 *
 * Created on March 27, 2003, 2:20 PM
 */

package com.yworks.yguard.test;

/**
 *
 * @author  Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public class StaticClassA
{
  
  public static int psi = 4;
  
  public int pi = 5;
  
  private Object hello = "Hello";
  
  /** Creates a new instance of StaticClassA */
  public StaticClassA()
  {
  }
  
  public static void staticObfuscatedMember(double foo){
    System.out.println("doing foofoo");
  }
  
  public static void staticMember(double foo){
    System.out.println("doing foo");
    staticObfuscatedMember(foo);
    StaticClassB.staticBarMember(foo);
  }
  
  public static void main(String[] args){
    new StaticClassA();
    new StaticClassB();
    StaticClassA.staticMember(1.234d);
    StaticClassB.staticBarMember(1.234d);
  }
}
