/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard.obf.classfile;

import java.io.*;
import java.util.*;

/**
 * Representation of an attribute.
 *
 * @author Mark Welsh
 */
public class EnclosingMethodAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2classIndex;
    private int u2nameAndTypeIndex;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Enclosing method attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
// Instance Methods ------------------------------------------------------
    protected EnclosingMethodAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_EnclosingMethod;
    }

    /**
     * Return the class index.  @return the class index
     */
    protected int getClassIndex() {return u2classIndex;}

    /**
     * Return the class index.  @param index the index
     */
    protected void setClassIndex(int index) { this.u2classIndex = index; }

    /**
     * Return the name-and-type index.  @return the name and type index
     */
    protected int getNameAndTypeIndex() {return u2nameAndTypeIndex;}

    /**
     * Set the name-and-type index.  @param index the index
     */
    protected void setNameAndTypeIndex(int index) {u2nameAndTypeIndex = index;}

    /**
     * Check for Utf8 references in the 'info' data to the constant pool and
     * mark them; over-ride this in sub-classes.
     */
    protected void markUtf8RefsInInfo(ConstantPool pool) {
      //nothing to be done ClassCpInfo and NameAndTypeCpInfo are handled and marked 
      //automatically - their Utf8 references need no marking
      // however we mark the NameAndTypeCpInfo here, although this should be done in markNTRefs however
      // there is no markNTRefs for this class that might be called anywhere :-(
      if (u2nameAndTypeIndex > 0){
        NameAndTypeCpInfo ntcpi = (NameAndTypeCpInfo) pool.getCpEntry(u2nameAndTypeIndex);
        ntcpi.incRefCount();
      }
      // does not seem to be necessary, since all ClassCpInfo will be remapped equally, so
      // the reference counting is never used for ClassCpInfo...
//      ClassCpInfo cpi = (ClassCpInfo) pool.getCpEntry(u2classIndex);
//      cpi.incRefCount();
    }
    
    
    

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2classIndex = din.readUnsignedShort();
        u2nameAndTypeIndex = din.readUnsignedShort();
    }

    /** Write the 'info' data following the u1tag byte. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2classIndex);
        dout.writeShort(u2nameAndTypeIndex);
    }

    /**
     * Dump the content of the class file to the specified file (used for debugging).  @param pw the pw
     *
     * @param cf    the cf
     * @param index the index
     */
    public void dump(PrintWriter pw, ClassFile cf, int index)
    {
        pw.println("  EnclosingMethod " );
//        + Integer.toString(index) + ": " + ((Utf8CpInfo)cf.getCpEntry(((ClassCpInfo)cf.getCpEntry(u2classIndex)).getNameIndex())).getString() +
//                   " " + ((Utf8CpInfo)cf.getCpEntry(((NameAndTypeCpInfo)cf.getCpEntry(u2nameAndTypeIndex)).getNameIndex())).getString() +
//                   " " + ((Utf8CpInfo)cf.getCpEntry(((NameAndTypeCpInfo)cf.getCpEntry(u2nameAndTypeIndex)).getDescriptorIndex())).getString());
    }
}

