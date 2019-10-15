/*
 * ParameterAnnotationInfo.java
 *
 * Created on April 20, 2005, 4:27 PM
 */

package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 *
 * @author muellese
 */
public class ParameterAnnotationInfo
{
  
  // Constants -------------------------------------------------------------
  
  
  // Fields ----------------------------------------------------------------
  private int u2annotationCount;
  private AnnotationInfo[] annotations;
  
  
  // Class Methods ---------------------------------------------------------
  public static ParameterAnnotationInfo create(DataInput din) throws java.io.IOException
  {
    if (din == null) throw new NullPointerException("DataInput cannot be null!");
    ParameterAnnotationInfo an = new ParameterAnnotationInfo();
    an.read(din);
    return an;
  }  
  
  // Instance Methods ------------------------------------------------------
  private ParameterAnnotationInfo()
  {}
  
  protected AnnotationInfo[] getAnnotations(){
    return annotations;
  }
  
  protected void markUtf8RefsInInfo(ConstantPool pool) {
    for (int i = 0; i < u2annotationCount; i++){
      annotations[i].markUtf8RefsInInfo(pool);
    }
  }

  
  private void read(DataInput din) throws java.io.IOException
  {
    u2annotationCount = din.readUnsignedShort();
    annotations = new AnnotationInfo[u2annotationCount];
    for (int i = 0; i < u2annotationCount; i++)
    {
      annotations[i] = AnnotationInfo.create(din);
    }
  }
  
  /** Export the representation to a DataOutput stream. */
  public void write(DataOutput dout) throws java.io.IOException
  {
    dout.writeShort(u2annotationCount);
    for (int i = 0; i < u2annotationCount; i++)
    {
      annotations[i].write(dout);
    }
  }
}
