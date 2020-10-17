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
public class SignatureAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2signatureIndex;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Signature attr info.
   *
   *
   *@param cf            the cf
   *
   *@param attrNameIndex the attr name index
   *
   *@param attrLength    the attr length
   */
// Instance Methods ------------------------------------------------------
  protected SignatureAttrInfo( ClassFile cf, int attrNameIndex, int attrLength ) {
    super(cf, attrNameIndex, attrLength);
  }

  /** Return the String name of the attribute; over-ride this in sub-classes. */
  protected String getAttrName() {
    return ATTR_Signature;
  }

  /**
   * Get signature index int.
   *
   * @return the int
   */
  protected int getSignatureIndex() {
    return this.u2signatureIndex;
  }

  /**
   * Set signature index.
   *
   *
   *@param index the index
   */
  protected void setSignatureIndex( int index ) {
    this.u2signatureIndex = index;
  }

  /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
  protected void markUtf8RefsInInfo( ConstantPool pool ) {
    pool.incRefCount(u2signatureIndex);
  }

  /** Read the data following the header. */
  protected void readInfo( DataInput din ) throws java.io.IOException {
    u2signatureIndex = din.readUnsignedShort();
  }

  /** Export data following the header to a DataOutput stream. */
  public void writeInfo( DataOutput dout ) throws java.io.IOException {
    dout.writeShort(u2signatureIndex);
  }

  public String toString() {
    return super.toString() + ((Utf8CpInfo) owner.getCpEntry(u2signatureIndex)).getString();
  }
}

