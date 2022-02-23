package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of the record attribute.
 *
 * @author Thomas Behr
 */
public class RecordAttrInfo extends AttrInfo {
  private RecordComponent[] components;

  /**
   * Instantiates a new record attr info.
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
  RecordAttrInfo(
    final ClassFile cf, final int attrNameIndex, final int attrLength
  ) {
    super(cf, attrNameIndex, attrLength);
    this.components = new RecordComponent[0];
  }

  // Instance Methods ------------------------------------------------------
  /**
   * Returns <code>"Record"</code>.
   */
  protected String getAttrName() {
    return ATTR_Record;
  }

  RecordComponent[] getComponents() {
    return components;
  }

  protected void markUtf8RefsInInfo( ConstantPool pool)  {
    for (int i = 0, n = components.length; i < n; ++i) {
      components[i].markUtf8Refs(pool);
    }
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    final int u2ComponentsCount = din.readUnsignedShort();
    components = new RecordComponent[u2ComponentsCount];
    for (int i = 0; i < u2ComponentsCount; ++i) {
      components[i] = RecordComponent.read(din, owner);
    }
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    final int u2ComponentsCount = components.length;
    dout.writeShort(u2ComponentsCount);
    for (int i = 0; i < u2ComponentsCount; ++i) {
      components[i].write(dout);
    }
  }
}
