package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of the module main class attribute. 
 * @author Thomas Behr
 */
public class ModuleMainClassAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2mainClassIndex;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------
  ModuleMainClassAttrInfo(
          final ClassFile cf, final int attrNameIndex, final int attrLength
  ) {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Returns <code>"ModuleMainClass"</code>.
   */
  protected String getAttrName() {
    return ATTR_ModuleMainClass;
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    u2mainClassIndex = din.readUnsignedShort();
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    dout.writeShort(u2mainClassIndex);
  }
}
