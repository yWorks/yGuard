package com.yworks.yguard.obf.classfile;

import java.io.DataInput;

/**
 * @author mfk
 */
public class RuntimeVisibleTypeAnnotationsAttrInfo extends AttrInfo {
  private int u2AnnotationCount;
  private TypeAnnotationInfo[] annotations;

  public RuntimeVisibleTypeAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength) {
    super(cf, attrNameIndex, attrLength);
  }

  protected String getAttrName() {
    return ClassConstants.ATTR_RuntimeVisibleTypeAnnotations;
  }

  public TypeAnnotationInfo[] getAnnotations() {
    return annotations;
  }

  public ClassFile getOwner() {
    return owner;
  }

  public void writeInfo(java.io.DataOutput dout) throws java.io.IOException {
    dout.writeShort(u2AnnotationCount);
    for (int i = 0; i < u2AnnotationCount; i++) {
      annotations[i].write(dout);
    }
  }

  protected void readInfo(java.io.DataInput din) throws java.io.IOException {
    u2AnnotationCount = din.readUnsignedShort();
    annotations = new TypeAnnotationInfo[u2AnnotationCount];
    for (int i = 0; i < u2AnnotationCount; i++) {
      annotations[i] = TypeAnnotationInfo.create(din);
    }
  }

  protected void markUtf8RefsInInfo(ConstantPool pool) {
    for (int i = 0; i < u2AnnotationCount; i++) {
      annotations[i].markUtf8RefsInInfo(pool);
    }
  }
}
