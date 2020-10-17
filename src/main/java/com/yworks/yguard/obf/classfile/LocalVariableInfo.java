/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of an Local Variable table entry.
 *
 * @author Mark Welsh
 */
public class LocalVariableInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2startpc;
  private int u2length;
  private int u2nameIndex;
  private int u2descriptorIndex;
  private int u2index;


  /**
   * Create local variable info.
   *
   * @param din the din
   * @return the local variable info
   * @throws IOException the io exception
   */
// Class Methods ---------------------------------------------------------
  public static LocalVariableInfo create( DataInput din ) throws IOException {
    if (din == null) {
      throw new NullPointerException("DataInput cannot be null!");
    }
    LocalVariableInfo lvi = new LocalVariableInfo();
    lvi.read(din);
    return lvi;
  }


  // Instance Methods ------------------------------------------------------
  private LocalVariableInfo() {
  }

  /**
   * Return name index into Constant Pool.
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
   * Return descriptor index into Constant Pool.
   *
   * @return the descriptor index
   */
  protected int getDescriptorIndex() {
    return u2descriptorIndex;
  }

  /**
   * Set the descriptor index.
   *
   * @param index the index
   */
  protected void setDescriptorIndex( int index ) {
    u2descriptorIndex = index;
  }

  /**
   * Check for Utf8 references to constant pool and mark them.
   *
   * @param pool the pool
   */
  protected void markUtf8Refs( ConstantPool pool ) {
    pool.incRefCount(u2nameIndex);
    pool.incRefCount(u2descriptorIndex);
  }

  private void read( DataInput din ) throws IOException {
    u2startpc = din.readUnsignedShort();
    u2length = din.readUnsignedShort();
    u2nameIndex = din.readUnsignedShort();
    u2descriptorIndex = din.readUnsignedShort();
    u2index = din.readUnsignedShort();
  }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write( DataOutput dout ) throws IOException {
    dout.writeShort(u2startpc);
    dout.writeShort(u2length);
    dout.writeShort(u2nameIndex);
    dout.writeShort(u2descriptorIndex);
    dout.writeShort(u2index);
  }
}
