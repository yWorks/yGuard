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
  /**
   * Size of components_count in bytes.
   */
  private static final int COMPONENTS_COUNT_FIELD_SIZE = 2;

  private RecordComponent[] components;
  private boolean attrInfoLengthDirty;

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

  protected int getAttrInfoLength() {
    if (attrInfoLengthDirty) {
      int newAttrLength = COMPONENTS_COUNT_FIELD_SIZE;
      for (int i = 0, m = components.length; i < m; ++i) {
        newAttrLength += componentLength(components[i]);
      }
      u4attrLength = newAttrLength;
      attrInfoLengthDirty = false;
    }
    return u4attrLength;
  }

  protected void trimAttrsExcept( final String[] keepAttrs ) {
    boolean dirty = false;
    for (int i = 0, n = components.length; i < n; ++i) {
      dirty |= components[i].trimAttrsExcept(keepAttrs);
    }
    attrInfoLengthDirty = dirty;
  }

  protected void markUtf8RefsInInfo( final ConstantPool pool )  {
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

  /**
   * Returns the length of the given component in bytes.
   */
  private static int componentLength( final RecordComponent c ) {
    int length = RecordComponent.CONSTANT_FIELD_SIZE;
    final AttrInfo[] attributes = c.getAttributes();
    for (int j = 0, n = attributes.length; j < n; ++j) {
      length += AttrInfo.CONSTANT_FIELD_SIZE;
      length += attributes[j].getAttrInfoLength();
    }
    return length;
  }
}
