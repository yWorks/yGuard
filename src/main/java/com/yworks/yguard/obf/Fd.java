/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard.obf;

import com.yworks.yguard.obf.classfile.*;

/**
 * Tree item representing a field.
 *
 * @author Mark Welsh
 */
public class Fd extends MdFd
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------

  /**
   * Ctor.
   *
   * @param parent            the parent
   * @param isSynthetic       the is synthetic
   * @param name              the name
   * @param descriptor        the descriptor
   * @param access            the access
   * @param obfuscationConfig the obfuscation config
   */
  public Fd(TreeItem parent, boolean isSynthetic, String name, String descriptor,
              int access, ObfuscationConfig obfuscationConfig)
    {
      super(parent, isSynthetic, name, descriptor, access, obfuscationConfig);
    }

    /** Return the display name of the descriptor types. */
    protected String getDescriptorName()
    {
        return ";";
    }

  /**
   * Is this field's name a match to the wildcard pattern?
   *
   * @param namePattern the name pattern
   * @return the boolean
   */
  public boolean isWildcardMatch(String namePattern) {
        return isMatch(namePattern, getFullInName());
    }

  /**
   * Is this field's name a non-recursive match to the wildcard pattern?
   *
   * @param namePattern the name pattern
   * @return the boolean
   */
  public boolean isNRWildcardMatch(String namePattern) {
        return isNRMatch(namePattern, getFullInName());
    }
}

