/*
 * RuntimeVisibleAnnotationsAttrInfo.java
 *
 * Created on April 20, 2005, 11:51 AM
 */

package com.yworks.yguard.obf.classfile;

/**
 * The type Runtime invisible annotations attr info.
 *
 * @author muellese
 */
public class RuntimeInvisibleAnnotationsAttrInfo extends RuntimeVisibleAnnotationsAttrInfo {
  /**
   * Creates a new instance of RuntimeVisibleAnnotationsAttrInfo
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
  public RuntimeInvisibleAnnotationsAttrInfo( ClassFile cf, int attrNameIndex, int attrLength ) {
    super(cf, attrNameIndex, attrLength);
  }

  protected String getAttrName() {
    return ClassConstants.ATTR_RuntimeInvisibleAnnotations;
  }
}
