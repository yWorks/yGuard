package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'invokedynamic' or 'dynamic' entry in the ConstantPool.
 * @author Thomas Behr
 */
public abstract class AbstractDynamicCpInfo extends CpInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2bootstrapMethodAttrIndex;
  private int u2nameAndTypeIndex;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------
  protected AbstractDynamicCpInfo( final int tag ) {
    super(tag);
  }

  protected void readInfo( final DataInput din ) throws IOException {
    u2bootstrapMethodAttrIndex = din.readUnsignedShort();
    u2nameAndTypeIndex = din.readUnsignedShort();
  }

  protected void writeInfo( final DataOutput dout ) throws IOException {
    dout.writeShort(u2bootstrapMethodAttrIndex);
    dout.writeShort(u2nameAndTypeIndex);
  }

  protected void markNTRefs( final ConstantPool pool ) {
    pool.incRefCount(u2nameAndTypeIndex);
  }

  public int getBootstrapMethodAttrIndex() {
    return u2bootstrapMethodAttrIndex;
  }

  public int getNameAndTypeIndex() {
    return u2nameAndTypeIndex;
  }

  public void setNameAndTypeIndex( final int index ) {
    this.u2nameAndTypeIndex = index;
  }
}
