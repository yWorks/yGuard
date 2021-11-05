/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

/**
 * Interface to a list of method and field names and descriptors -- used for checking
 * if a name/descriptor is in the public/protected lists of the super-class/interface
 * hierarchy.
 *
 * @author Mark Welsh
 */
public interface NameListUp
{
  /**
   * Get output method name from list, or null if no mapping exists.
   *
   * @param name       the name
   * @param descriptor the descriptor
   * @return the method out name up
   * @throws ClassNotFoundException the class not found exception
   */
  public String getMethodOutNameUp(String name, String descriptor) throws ClassNotFoundException;

  /**
   * Get obfuscated method name from list, or null if no mapping exists.
   *
   * @param name       the name
   * @param descriptor the descriptor
   * @return the method obf name up
   * @throws ClassNotFoundException the class not found exception
   */
  public String getMethodObfNameUp(String name, String descriptor) throws ClassNotFoundException;

  /**
   * Get output field name from list, or null if no mapping exists.
   *
   * @param name the name
   * @return the field out name up
   * @throws ClassNotFoundException the class not found exception
   */
  public String getFieldOutNameUp(String name) throws ClassNotFoundException;

  /**
   * Get obfuscated field name from list, or null if no mapping exists.
   *
   * @param name the name
   * @return the field obf name up
   * @throws ClassNotFoundException the class not found exception
   */
  public String getFieldObfNameUp(String name) throws ClassNotFoundException;
}

