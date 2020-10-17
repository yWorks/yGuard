/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of an Line Number table entry.
 *
 * @author Mark Welsh
 */
public class LineNumberInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2startpc;
  private int u2lineNumber;

  /**
   * Instantiates a new Line number info.
   *
   * @param startPC    the start pc
   * @param lineNumber the line number
   */
  public LineNumberInfo( int startPC, int lineNumber ) {
    setLineNumber(lineNumber);
    setStartPC(startPC);
  }


  /**
   * Create line number info.
   *
   * @param din the din
   * @return the line number info
   * @throws IOException the io exception
   */
// Class Methods ---------------------------------------------------------
  public static LineNumberInfo create( DataInput din ) throws java.io.IOException {
    LineNumberInfo lni = new LineNumberInfo();
    lni.read(din);
    return lni;
  }

  /**
   * Set line number.
   *
   * @param number the number
   */
  public void setLineNumber( int number ) {
    this.u2lineNumber = number;
  }

  /**
   * Get line number int.
   *
   * @return the int
   */
  public int getLineNumber() {
    return this.u2lineNumber;
  }

  /**
   * Get start pc int.
   *
   * @return the int
   */
  public int getStartPC() {
    return this.u2startpc;
  }

  /**
   * Set start pc.
   *
   * @param startPc the start pc
   */
  public void setStartPC( int startPc ) {
    this.u2startpc = startPc;
  }


  /**
   * Instantiates a new Line number info.
   */
// Instance Methods ------------------------------------------------------
  public LineNumberInfo() {
  }

  private void read( DataInput din ) throws java.io.IOException {
    u2startpc = din.readUnsignedShort();
    u2lineNumber = din.readUnsignedShort();
  }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write( DataOutput dout ) throws java.io.IOException {
    dout.writeShort(u2startpc);
    dout.writeShort(u2lineNumber);
  }
}
