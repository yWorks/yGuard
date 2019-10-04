package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of the nest members attribute. 
 * @author Thomas Behr
 */
public class NestMembersAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int[] u2classes;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------
  NestMembersAttrInfo(
          final ClassFile cf, final int attrNameIndex, final int attrLength
  ) {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Returns <code>"NestMembers"</code>.
   */
  protected String getAttrName() {
    return ATTR_NestMembers;
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    final int u2numberOfClasses = din.readUnsignedShort();
    u2classes = new int[u2numberOfClasses];
    for (int i = 0; i < u2numberOfClasses; ++i) {
      u2classes[i] = din.readUnsignedShort();
    }
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    final int u2numberOfClasses = u2classes.length;
    dout.writeShort(u2numberOfClasses);
    for (int i = 0; i < u2numberOfClasses; ++i) {
      dout.writeShort(u2classes[i]);
    }
  }
}
