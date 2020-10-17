package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'methodhandle' entry in the ConstantPool.
 *
 * @author Sebastian Rheinnecker, yworks
 */
public class MethodHandleCpInfo extends CpInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u1referenceKind;
  private int u2referenceIndex;

  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Method handle cp info.
   */
// Instance Methods ------------------------------------------------------
  protected MethodHandleCpInfo() {
    super(CONSTANT_MethodHandle);
  }

  protected void readInfo( DataInput din ) throws IOException {
    u1referenceKind = din.readUnsignedByte();
    u2referenceIndex = din.readUnsignedShort();
  }

  protected void writeInfo( DataOutput dout ) throws IOException {
    dout.writeByte(u1referenceKind);
    dout.writeShort(u2referenceIndex);
  }

  /**
   * Gets reference kind.
   *
   * @return the reference kind
   */
  protected int getReferenceKind() {
    return u1referenceKind;
  }

  /**
   * Gets reference index.
   *
   * @return the reference index
   */
  protected int getReferenceIndex() {
    return u2referenceIndex;
  }
}
