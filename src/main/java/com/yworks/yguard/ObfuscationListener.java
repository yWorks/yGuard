/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard;

public interface ObfuscationListener extends java.util.EventListener
{
  
  void obfuscatingJar(String inJar, String outJar);
  
  void obfuscatingClass(String className);
  
  void parsingClass(String className);
  
  void parsingJar(String jar);
  
}

