package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of the nest host attribute. 
 * @author Thomas Behr
 */
public class NestHostAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2hostClassIndex;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------
  NestHostAttrInfo(
          final ClassFile cf, final int attrNameIndex, final int attrLength
  ) {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Returns <code>"NestHost"</code>.
   */
  protected String getAttrName() {
    return ATTR_NestHost;
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    u2hostClassIndex = din.readUnsignedShort();
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    dout.writeShort(u2hostClassIndex);
  }
}
