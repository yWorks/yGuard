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
public class RuntimeVisibleAnnotationsAttrInfo extends AttrInfo
{
  private int u2AnnotationCount;
  private AnnotationInfo[] annotations;
  
  /** Creates a new instance of RuntimeVisibleAnnotationsAttrInfo */
  public RuntimeVisibleAnnotationsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
  {
    super(cf, attrNameIndex, attrLength);
  }

  protected String getAttrName()
  {
    return ClassConstants.ATTR_RuntimeVisibleAnnotations;
  }
  
  public AnnotationInfo[] getAnnotations(){
    return annotations;
  }

  public ClassFile getOwner(){
    return owner;
  }

  public int getU2TypeIndex(int annotationIndex){
    return annotations[annotationIndex].u2typeIndex;
  }

  public void writeInfo(java.io.DataOutput dout) throws java.io.IOException
  {
    dout.writeShort(u2AnnotationCount);
    for (int i = 0; i < u2AnnotationCount; i++){
      annotations[i].write(dout);
    }
  }

  protected void readInfo(java.io.DataInput din) throws java.io.IOException
  {
    u2AnnotationCount = din.readUnsignedShort();
    annotations = new AnnotationInfo[u2AnnotationCount];
    for (int i = 0; i < u2AnnotationCount; i++){
      annotations[i] = AnnotationInfo.create(din);
    }
  }

    protected void markUtf8RefsInInfo(ConstantPool pool) {
      for (int i = 0; i < u2AnnotationCount; i++){
        annotations[i].markUtf8RefsInInfo(pool);
      }
    }
}
