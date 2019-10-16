/*
 * RuntimeVisibleAnnotationsAttrInfo.java
 *
 * Created on April 20, 2005, 11:51 AM
 */

package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author muellese
 */
public class RuntimeInvisibleAnnotationsAttrInfo extends RuntimeVisibleAnnotationsAttrInfo
{
  /** Creates a new instance of RuntimeVisibleAnnotationsAttrInfo */
  public RuntimeInvisibleAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    super(cf, attrNameIndex, attrLength);
  }

  protected String getAttrName()
  {
    return ClassConstants.ATTR_RuntimeInvisibleAnnotations;
  }
}
