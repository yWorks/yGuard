/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Representation of a 'double' entry in the ConstantPool (takes up two indices).
 *
 * @author Mark Welsh
 */
public class DoubleCpInfo extends CpInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u4highBytes;
  private int u4lowBytes;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Double cp info.
   */
// Instance Methods ------------------------------------------------------
  protected DoubleCpInfo() {
    super(CONSTANT_Double);
  }


  /** Read the 'info' data following the u1tag byte. */
  protected void readInfo( DataInput din ) throws java.io.IOException {
    u4highBytes = din.readInt();
    u4lowBytes = din.readInt();
  }

  /** Write the 'info' data following the u1tag byte. */
  protected void writeInfo( DataOutput dout ) throws java.io.IOException {
    dout.writeInt(u4highBytes);
    dout.writeInt(u4lowBytes);
  }
}
