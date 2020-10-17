package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'methodtype' entry in the ConstantPool.
 *
 * @author Sebastian Rheinnecker, yworks
 */
public class MethodTypeCpInfo extends CpInfo {
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2descriptorIndex;

    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Method type cp info.
     */
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

    /**
     * Gets u 2 descriptor index.
     *
     * @return the u 2 descriptor index
     */
    public int getU2descriptorIndex() {
    return u2descriptorIndex;
  }

  protected void markUtf8Refs(ConstantPool pool) {
    pool.incRefCount(this.u2descriptorIndex);
  }

    /**
     * Sets u 2 descriptor index.
     *
     * @param u2descriptorIndex the u 2 descriptor index
     */
    public void setU2descriptorIndex(int u2descriptorIndex) {
    this.u2descriptorIndex = u2descriptorIndex;
  }
}
