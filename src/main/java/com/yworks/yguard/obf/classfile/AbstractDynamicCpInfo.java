package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'invokedynamic' or 'dynamic' entry in the ConstantPool.
 *
 * @author Thomas Behr
 */
public abstract class AbstractDynamicCpInfo extends CpInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2bootstrapMethodAttrIndex;
  private int u2nameAndTypeIndex;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Abstract dynamic cp info.
   *
   * @param tag the tag
   */
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

  /**
   * Gets bootstrap method attr index.
   *
   * @return the bootstrap method attr index
   */
  public int getBootstrapMethodAttrIndex() {
    return u2bootstrapMethodAttrIndex;
  }

  /**
   * Gets name and type index.
   *
   * @return the name and type index
   */
  public int getNameAndTypeIndex() {
    return u2nameAndTypeIndex;
  }

  /**
   * Sets name and type index.
   *
   * @param index the index
   */
  public void setNameAndTypeIndex( final int index ) {
    this.u2nameAndTypeIndex = index;
  }
}
