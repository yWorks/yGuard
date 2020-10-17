/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard;

/**
 * The interface Obfuscation listener.
 */
public interface ObfuscationListener extends java.util.EventListener {

  /**
   * Obfuscating jar.
   *
   *
   *@param inJar  the in jar
   *
   *@param outJar the out jar
   */
  void obfuscatingJar( String inJar, String outJar );

  /**
   * Obfuscating class.
   *
   *
   *@param className the class name
   */
  void obfuscatingClass( String className );

  /**
   * Parsing class.
   *
   *
   *@param className the class name
   */
  void parsingClass( String className );

  /**
   * Parsing jar.
   *
   *
   *@param jar the jar
   */
  void parsingJar( String jar );

}

