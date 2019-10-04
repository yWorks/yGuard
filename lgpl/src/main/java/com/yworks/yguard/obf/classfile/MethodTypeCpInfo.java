package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'methodtype' entry in the ConstantPool.
 *
 * @author      Sebastian Rheinnecker, yworks
 */
public class MethodTypeCpInfo extends CpInfo {
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2descriptorIndex;

    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected MethodTypeCpInfo()
    {
      super(CONSTANT_MethodType);
    }

    protected void readInfo(DataInput din) throws IOException {
      u2descriptorIndex = din.readUnsignedShort();
    }

    protected void writeInfo(DataOutput dout) throws IOException {
      dout.writeShort(u2descriptorIndex);
    }

  public int getU2descriptorIndex() {
    return u2descriptorIndex;
  }

  protected void markUtf8Refs(ConstantPool pool) {
    pool.incRefCount(this.u2descriptorIndex);
  }

  public void setU2descriptorIndex(int u2descriptorIndex) {
    this.u2descriptorIndex = u2descriptorIndex;
  }
}
