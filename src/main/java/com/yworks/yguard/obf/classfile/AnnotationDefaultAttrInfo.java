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
  
  /** Creates a new instance of AnnotationDefaultAttrInfo 
   * @param cf ClassFile instance
   * @param attrNameIndex Attribute name index
   * @param attrLength Attribute length
   */
  public AnnotationDefaultAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    super(cf, attrNameIndex, attrLength);
  }
  
  /**
   * @return String Attribute name
   */
  protected String getAttrName()
  {
    return ClassConstants.ATTR_AnnotationDefault;
  }

  /**
   * @param dout DataOutput stream
   * @throws IOException
   */
  public void writeInfo(java.io.DataOutput dout) throws java.io.IOException
  {
    elementValue.write(dout);
  }
  
  
  /**
   * @param din DataInput stream
   * @throws IOException
   */
  protected void readInfo(java.io.DataInput din) throws java.io.IOException
  {
    elementValue = ElementValueInfo.create(din);
  }

  /**
   * @param pool ConstantPool instance
   */
  protected void markUtf8RefsInInfo(ConstantPool pool)
  {
    elementValue.markUtf8RefsInInfo(pool);
  }
}
