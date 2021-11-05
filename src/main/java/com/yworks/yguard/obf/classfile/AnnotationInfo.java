package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The type Annotation info.
 */
public class AnnotationInfo
{
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  /**
   * The type index.
   */
  protected int u2typeIndex;
  private int u2elementCount;
  private ElementValuePairInfo[] elementValuePairs;


  // Class Methods ---------------------------------------------------------
  /**
   * Create annotation info.
   *
   * @param din the din
   * @return the annotation info
   * @throws IOException the io exception
   */
  public static AnnotationInfo create(DataInput din) throws java.io.IOException
  {
    if (din == null) throw new NullPointerException("DataInput cannot be null!");
    AnnotationInfo an = new AnnotationInfo();
    an.read(din);
    return an;
  }

  private AnnotationInfo()
  {}

  // Instance Methods ------------------------------------------------------
  /**
   * Get element value pairs element value pair info [ ].
   *
   * @return the element value pair info [ ]
   */
  public ElementValuePairInfo[] getElementValuePairs(){
    return elementValuePairs;
  }
  
  /**
   * Mark utf 8 refs in info.
   *
   * @param pool the pool
   */
  protected void markUtf8RefsInInfo(ConstantPool pool) {
    pool.getCpEntry(u2typeIndex).incRefCount();
    for (int i = 0; i < u2elementCount; i++){
      elementValuePairs[i].markUtf8RefsInInfo(pool);
    }
  }
  
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

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
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
