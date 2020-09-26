package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'class', 'module', or 'package' entry in the ConstantPool.
 * @author Thomas Behr
 */
public abstract class AbstractTypeCpInfo extends CpInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  int u2nameIndex;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------
  /**
  * @param tag CpInfo tag
  */
  protected AbstractTypeCpInfo(int tag) {
    super(tag);
  }

  /** Return the name index.
  * @return u2nameIndex the name index 
  */
  protected int getNameIndex() {
    return u2nameIndex;
  }

  /** Set the name index. 
  *@param index the name index
  */
  protected void setNameIndex(int index) {
    u2nameIndex = index;
  }

  /** Check for Utf8 references to constant pool and mark them.
  * @ param pool ConstantPool instance
  */
  protected void markUtf8Refs(ConstantPool pool) {
    pool.incRefCount(u2nameIndex);
  }

  /** Read the 'info' data following the u1tag byte. 
  * @param din DataInput instance
  * @throws IOException
  */
  protected void readInfo(DataInput din) throws IOException {
    u2nameIndex = din.readUnsignedShort();
  }

  /** Write the 'info' data following the u1tag byte.
  * @param dout DataOutput stream
  * @throws IOException
  */
  protected void writeInfo(DataOutput dout) throws IOException {
    dout.writeShort(u2nameIndex);
  }
}
