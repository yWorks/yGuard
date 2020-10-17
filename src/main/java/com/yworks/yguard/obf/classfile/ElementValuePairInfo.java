/*
 * ElementValueInfo.java
 *
 * Created on April 20, 2005, 4:19 PM
 */

package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The type Element value pair info.
 */
public class ElementValuePairInfo {
  /**
   * The U 2 element name index.
   */
  protected int u2ElementNameIndex;
  /**
   * The Element value.
   */
  protected ElementValueInfo elementValue;

  private ElementValuePairInfo() {
  }

  /**
   * Create element value pair info.
   *
   * @param din the din
   * @return the element value pair info
   * @throws IOException the io exception
   */
  public static ElementValuePairInfo create( DataInput din ) throws IOException {
    ElementValuePairInfo evp = new ElementValuePairInfo();
    evp.read(din);
    return evp;
  }

  /**
   * Read.
   *
   * @param din the din
   * @throws IOException the io exception
   */
  protected void read( DataInput din ) throws java.io.IOException {
    u2ElementNameIndex = din.readUnsignedShort();
    elementValue = ElementValueInfo.create(din);
  }

  /**
   * Mark utf 8 refs in info.
   *
   * @param pool the pool
   */
  protected void markUtf8RefsInInfo( ConstantPool pool ) {
    pool.getCpEntry(u2ElementNameIndex).incRefCount();
    elementValue.markUtf8RefsInInfo(pool);
  }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write( DataOutput dout ) throws java.io.IOException {
    dout.writeShort(u2ElementNameIndex);
    elementValue.write(dout);
  }

  /**
   * Gets u 2 element name index.
   *
   * @return the u 2 element name index
   */
  public int getU2ElementNameIndex() {
    return u2ElementNameIndex;
  }

  /**
   * Gets element value.
   *
   * @return the element value
   */
  public ElementValueInfo getElementValue() {
    return elementValue;
  }
}
