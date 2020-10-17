package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'class', 'module', or 'package' entry in the ConstantPool.
 *
 * @author Thomas Behr
 */
public abstract class AbstractTypeCpInfo extends CpInfo {
  // Constants -------------------------------------------------------------


  /**
   * The U 2 name index.
   */
// Fields ----------------------------------------------------------------
  int u2nameIndex;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Abstract type cp info.
   *
   * @param tag the tag
   */
// Instance Methods ------------------------------------------------------
  protected AbstractTypeCpInfo( int tag ) {
    super(tag);
  }

  /**
   * Return the name index.
   *
   * @return the name index
   */
  protected int getNameIndex() {
    return u2nameIndex;
  }

  /**
   * Set the name index.
   *
   * @param index the index
   */
  protected void setNameIndex( int index ) {
    u2nameIndex = index;
  }

  /**
   * Check for Utf8 references to constant pool and mark them.
   */
  protected void markUtf8Refs( ConstantPool pool ) {
    pool.incRefCount(u2nameIndex);
  }

  /**
   * Read the 'info' data following the u1tag byte.
   */
  protected void readInfo( DataInput din ) throws IOException {
    u2nameIndex = din.readUnsignedShort();
  }

  /**
   * Write the 'info' data following the u1tag byte.
   */
  protected void writeInfo( DataOutput dout ) throws IOException {
    dout.writeShort(u2nameIndex);
  }
}
