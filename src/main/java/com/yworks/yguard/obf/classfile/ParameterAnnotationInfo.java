/*
 * ParameterAnnotationInfo.java
 *
 * Created on April 20, 2005, 4:27 PM
 */

package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The type Parameter annotation info.
 *
 * @author muellese
 */
public class ParameterAnnotationInfo {

  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2annotationCount;
  private AnnotationInfo[] annotations;


  /**
   * Create parameter annotation info.
   *
   * @param din the din
   * @return the parameter annotation info
   * @throws IOException the io exception
   */
// Class Methods ---------------------------------------------------------
  public static ParameterAnnotationInfo create( DataInput din ) throws java.io.IOException {
    if (din == null) {
      throw new NullPointerException("DataInput cannot be null!");
    }
    ParameterAnnotationInfo an = new ParameterAnnotationInfo();
    an.read(din);
    return an;
  }

  // Instance Methods ------------------------------------------------------
  private ParameterAnnotationInfo() {
  }

  /**
   * Get annotations annotation info [ ].
   *
   * @return the annotation info [ ]
   */
  protected AnnotationInfo[] getAnnotations() {
    return annotations;
  }

  /**
   * Mark utf 8 refs in info.
   *
   * @param pool the pool
   */
  protected void markUtf8RefsInInfo( ConstantPool pool ) {
    for (int i = 0; i < u2annotationCount; i++) {
      annotations[i].markUtf8RefsInInfo(pool);
    }
  }


  private void read( DataInput din ) throws java.io.IOException {
    u2annotationCount = din.readUnsignedShort();
    annotations = new AnnotationInfo[u2annotationCount];
    for (int i = 0; i < u2annotationCount; i++) {
      annotations[i] = AnnotationInfo.create(din);
    }
  }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write( DataOutput dout ) throws java.io.IOException {
    dout.writeShort(u2annotationCount);
    for (int i = 0; i < u2annotationCount; i++) {
      annotations[i].write(dout);
    }
  }
}
