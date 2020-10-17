/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Representation of an attribute.
 *
 * @author Mark Welsh
 */
public class LineNumberTableAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2lineNumberTableLength;
  private LineNumberInfo[] lineNumberTable;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Line number table attr info.
   *
   *
   *@param cf            the cf
   *
   *@param attrNameIndex the attr name index
   *
   *@param attrLength    the attr length
   */
// Instance Methods ------------------------------------------------------
  protected LineNumberTableAttrInfo( ClassFile cf, int attrNameIndex, int attrLength ) {
    super(cf, attrNameIndex, attrLength);
  }

  /** Return the String name of the attribute; over-ride this in sub-classes. */
  protected String getAttrName() {
    return ATTR_LineNumberTable;
  }

  /**
   * Get line number table line number info [ ].
   *
   * @return the line number info [ ]
   */
  public LineNumberInfo[] getLineNumberTable() {
    return lineNumberTable;
  }

  /**
   * Set line number table.
   *
   *
   *@param table the table
   */
  public void setLineNumberTable( LineNumberInfo[] table ) {
    this.lineNumberTable = table;
    this.u2lineNumberTableLength = this.lineNumberTable.length;
    this.u4attrLength = 2 + 4 * u2lineNumberTableLength;
  }

  /** Read the data following the header. */
  protected void readInfo( DataInput din ) throws java.io.IOException {
    u2lineNumberTableLength = din.readUnsignedShort();
    lineNumberTable = new LineNumberInfo[u2lineNumberTableLength];
    for (int i = 0; i < u2lineNumberTableLength; i++) {
      lineNumberTable[i] = LineNumberInfo.create(din);
    }
  }

  /** Export data following the header to a DataOutput stream. */
  public void writeInfo( DataOutput dout ) throws java.io.IOException {
    dout.writeShort(u2lineNumberTableLength);
    for (int i = 0; i < u2lineNumberTableLength; i++) {
      lineNumberTable[i].write(dout);
    }
  }
}

