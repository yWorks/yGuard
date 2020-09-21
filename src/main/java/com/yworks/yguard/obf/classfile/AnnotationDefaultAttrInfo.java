/*
 * RuntimeVisibleAnnotationsAttrInfo.java
 *
 * Created on April 20, 2005, 11:51 AM
 */

package com.yworks.yguard.obf.classfile;

/**
 *
 * @author muellese
 */
public class AnnotationDefaultAttrInfo extends AttrInfo
{
  protected ElementValueInfo elementValue;
  
  /** Creates a new instance of AnnotationDefaultAttrInfo */
  public AnnotationDefaultAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    super(cf, attrNameIndex, attrLength);
  }
  
  protected String getAttrName()
  {
    return ClassConstants.ATTR_AnnotationDefault;
  }
  
  public void writeInfo(java.io.DataOutput dout) throws java.io.IOException
  {
    elementValue.write(dout);
  }
  
  
  
  protected void readInfo(java.io.DataInput din) throws java.io.IOException
  {
    elementValue = ElementValueInfo.create(din);
  }

  protected void markUtf8RefsInInfo(ConstantPool pool)
  {
    elementValue.markUtf8RefsInInfo(pool);
  }
}
