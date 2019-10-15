package com.yworks.yguard.obf.classfile;

/**
 * @author mfk
 */
public class RuntimeInvisibleTypeAnnotationsAttrInfo extends RuntimeVisibleTypeAnnotationsAttrInfo {
  
  public RuntimeInvisibleTypeAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength) {
    super(cf, attrNameIndex, attrLength);
  }
  
  protected String getAttrName() {
    return ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations;
  }
  
}
