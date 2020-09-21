/*
 * RuntimeInvisibleParameterAnnotationsAttrInfo.java
 *
 * Created on April 20, 2005, 4:31 PM
 */

package com.yworks.yguard.obf.classfile;

/**
 *
 * @author muellese
 */
public class RuntimeInvisibleParameterAnnotationsAttrInfo extends RuntimeVisibleParameterAnnotationsAttrInfo
{
  
  /** Creates a new instance of RuntimeInvisibleParameterAnnotationsAttrInfo */
  public RuntimeInvisibleParameterAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    super(cf, attrNameIndex, attrLength);
  }

  protected String getAttrName()
  {
    return ClassConstants.ATTR_RuntimeInvisibleParameterAnnotations;
  }
}
