/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf;

/**
 * Placeholder class -- used to represent a class which has inner classes, before the
 * class itself has been encountered.
 *
 * @author Mark Welsh
 */
public class PlaceholderCl extends Cl {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------

  /**
   * Ctor.
   *
   * @param parent       the parent
   * @param isInnerClass the is inner class
   * @param name         the name
   */
  public PlaceholderCl( TreeItem parent, boolean isInnerClass, String name ) {
    super(parent, isInnerClass, name, null, null, 0, null);
  }
}

