/*
 * AnnotationInfo.java
 *
 * Created on April 20, 2005, 4:18 PM
 */

package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

public class AnnotationInfo
{
  // Constants -------------------------------------------------------------
  
  
  // Fields ----------------------------------------------------------------
  protected int u2typeIndex;
  private int u2elementCount;
  private ElementValuePairInfo[] elementValuePairs;
  
  
  // Class Methods ---------------------------------------------------------
  /**
   * @param din DataInput stream
   * @return AnnotationInfo
   * @throws IOException
   */
  public static AnnotationInfo create(DataInput din) throws java.io.IOException
  {
    if (din == null) throw new NullPointerException("DataInput cannot be null!");
    AnnotationInfo an = new AnnotationInfo();
    an.read(din);
    return an;
  }
  
  /**
   * @return ElementValuePairInfo[]
   */
  public ElementValuePairInfo[] getElementValuePairs(){
    return elementValuePairs;
  }
  
  // Instance Methods ------------------------------------------------------
  private AnnotationInfo()
  {}

  /**
   * @param pool ConstantPool instance
   */
  protected void markUtf8RefsInInfo(ConstantPool pool) {
    pool.getCpEntry(u2typeIndex).incRefCount();
    for (int i = 0; i < u2elementCount; i++){
      elementValuePairs[i].markUtf8RefsInInfo(pool);
    }
  }
  
  /**
   * @param din DataInput stream
   * @throws IOException
   */
  private void read(DataInput din) throws java.io.IOException
  {
    u2typeIndex = din.readUnsignedShort();
    u2elementCount = din.readUnsignedShort();
    elementValuePairs = new ElementValuePairInfo[u2elementCount];
    for (int i = 0; i < u2elementCount; i++)
    {
      elementValuePairs[i] = ElementValuePairInfo.create(din);
    }
  }
  
  /** Export the representation to a DataOutput stream. 
  * @param dout DataOutput stream
  * @throws IOException
  */
  public void write(DataOutput dout) throws java.io.IOException
  {
    dout.writeShort(u2typeIndex);
    dout.writeShort(u2elementCount);
    for (int i = 0; i < u2elementCount; i++)
    {
      elementValuePairs[i].write(dout);
    }
  }
}
