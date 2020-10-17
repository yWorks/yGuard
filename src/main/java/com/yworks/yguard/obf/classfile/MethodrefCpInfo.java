/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 *

 *
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'methodref' entry in the ConstantPool.
 *
 * @author Mark Welsh
 */
public class MethodrefCpInfo extends RefCpInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Methodref cp info.
   */
// Instance Methods ------------------------------------------------------
  protected MethodrefCpInfo() {
    super(CONSTANT_Methodref);
  }
}
