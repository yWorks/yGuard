package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a record component struct in the record attribute section.
 *
 * @author Thomas Behr
 */
public class RecordComponent {
  /**
   * Size of name_index + descriptor_index + attributes_count in bytes.
   */
  static final int CONSTANT_FIELD_SIZE = 6;

  int u2nameIndex;
  int u2descriptorIndex;
  AttrInfo[] attributes;

  private RecordComponent(
    final int nameIndex, final int descriptorIndex, final AttrInfo[] attributes
  ) {
    this.u2nameIndex = nameIndex;
    this.u2descriptorIndex = descriptorIndex;
    this.attributes = attributes;
  }

  int getNameIndex() {
    return u2nameIndex;
  }

  void setNameIndex( final int index ) {
    this.u2nameIndex = index;
  }

  int getDescriptorIndex() {
    return u2descriptorIndex;
  }

  void setDescriptorIndex( final int index ) {
    this.u2descriptorIndex = index;
  }

  AttrInfo[] getAttributes() {
    return attributes;
  }

  boolean trimAttrsExcept( final String[] keepAttrs ) {
    final int oldAttributesCount = attributes.length;
    attributes = AttrInfo.filter(attributes, keepAttrs);
    return oldAttributesCount != attributes.length;
  }

  void markUtf8Refs( final ConstantPool pool ) {
    pool.incRefCount(u2nameIndex);
    pool.incRefCount(u2descriptorIndex);
    for (int i = 0, n = attributes.length; i < n; ++i) {
      attributes[i].markUtf8Refs(pool);
    }
  }

  static RecordComponent read(
    final DataInput din, final ClassFile cf
  ) throws IOException {
    final int nameIndex = din.readUnsignedShort();
    final int descriptorIndex = din.readUnsignedShort();

    final int attributesCount = din.readUnsignedShort();
    final AttrInfo[] attributes = new AttrInfo[attributesCount];
    for (int i = 0; i < attributesCount; ++i) {
      attributes[i] = AttrInfo.create(din, cf);
    }

    return new RecordComponent(nameIndex, descriptorIndex, attributes);
  }

  void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2nameIndex);
    dout.writeShort(u2descriptorIndex);

    final int u2attributesCount = attributes.length;
    dout.writeShort(u2attributesCount);
    for (int i = 0; i < u2attributesCount; ++i) {
      attributes[i].write(dout);
    }
  }
}
