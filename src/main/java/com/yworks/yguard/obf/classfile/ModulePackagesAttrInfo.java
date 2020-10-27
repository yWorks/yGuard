package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a module packages attribute.
 *
 * @author Thomas Behr
 */
public class ModulePackagesAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int[] u2packageIndex;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Module packages attr info.
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
// Instance Methods ------------------------------------------------------
  ModulePackagesAttrInfo(
          final ClassFile cf, final int attrNameIndex, final int attrLength
  ) {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Returns <code>"ModulePackages"</code>.
   */
  protected String getAttrName() {
    return ATTR_ModulePackages;
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    final int u2packageCount = din.readUnsignedShort();
    u2packageIndex = new int[u2packageCount];
    for (int i = 0; i < u2packageCount; ++i) {
      u2packageIndex[i] = din.readUnsignedShort();
    }
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    final int u2packageCount = u2packageIndex.length;
    dout.writeShort(u2packageCount);
    for (int i = 0; i < u2packageCount; ++i) {
      dout.writeShort(u2packageIndex[i]);
    }
  }
}
