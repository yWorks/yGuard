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
public class LocalVariableTypeTableAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2localVariableTypeTableLength;
  private LocalVariableTypeInfo[] localVariableTypeTable;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Local variable type table attr info.
   *
   *
   *@param cf            the cf
   *
   *@param attrNameIndex the attr name index
   *
   *@param attrLength    the attr length
   */
// Instance Methods ------------------------------------------------------
  protected LocalVariableTypeTableAttrInfo( ClassFile cf, int attrNameIndex, int attrLength ) {
    super(cf, attrNameIndex, attrLength);
  }

  /** Return the String name of the attribute; over-ride this in sub-classes. */
  protected String getAttrName() {
    return ATTR_LocalVariableTypeTable;
  }

  /**
   * Return the array of local variable table entries.  @return the local variable type info [ ]
   */
  protected LocalVariableTypeInfo[] getLocalVariableTypeTable() {
    return localVariableTypeTable;
  }

  /**
   * Sets local variable type table.
   *
   *
   *@param lvts the lvts
   */
  public void setLocalVariableTypeTable( LocalVariableTypeInfo[] lvts ) {
    this.localVariableTypeTable = lvts;
    this.u2localVariableTypeTableLength = lvts.length;
    this.u4attrLength = 2 + 10 * u2localVariableTypeTableLength;
  }

  /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
  protected void markUtf8RefsInInfo( ConstantPool pool ) {
    for (int i = 0; i < localVariableTypeTable.length; i++) {
      localVariableTypeTable[i].markUtf8Refs(pool);
    }
  }

  /** Read the data following the header. */
  protected void readInfo( DataInput din ) throws java.io.IOException {
    u2localVariableTypeTableLength = din.readUnsignedShort();
    localVariableTypeTable = new LocalVariableTypeInfo[u2localVariableTypeTableLength];
    for (int i = 0; i < u2localVariableTypeTableLength; i++) {
      localVariableTypeTable[i] = LocalVariableTypeInfo.create(din);
    }
  }

  /** Export data following the header to a DataOutput stream. */
  public void writeInfo( DataOutput dout ) throws java.io.IOException {
    dout.writeShort(u2localVariableTypeTableLength);
    for (int i = 0; i < u2localVariableTypeTableLength; i++) {
      localVariableTypeTable[i].write(dout);
    }
  }

}

