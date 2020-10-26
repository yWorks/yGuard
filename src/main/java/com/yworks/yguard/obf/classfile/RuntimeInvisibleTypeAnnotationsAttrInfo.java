package com.yworks.yguard.obf.classfile;

/**
 * The type Runtime invisible type annotations attr info.
 *
 * @author mfk
 */
public class RuntimeInvisibleTypeAnnotationsAttrInfo extends RuntimeVisibleTypeAnnotationsAttrInfo {

    /**
     * Instantiates a new Runtime invisible type annotations attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
    public RuntimeInvisibleTypeAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength) {
    super(cf, attrNameIndex, attrLength);
  }
  
  protected String getAttrName() {
    return ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations;
  }
  
}
