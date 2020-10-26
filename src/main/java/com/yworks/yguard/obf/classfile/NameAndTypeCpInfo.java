/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Representation of a 'nameandtype' entry in the ConstantPool.
 *
 * @author Mark Welsh
 */
public class NameAndTypeCpInfo extends CpInfo implements Cloneable
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2nameIndex;
    private int u2descriptorIndex;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Name and type cp info.
     */
// Instance Methods ------------------------------------------------------
    protected NameAndTypeCpInfo()
    {
        super(CONSTANT_NameAndType);
    }

    /** Clone the entry. */
    public Object clone()
    {
        NameAndTypeCpInfo cloneInfo = new NameAndTypeCpInfo();
        cloneInfo.u2nameIndex = this.u2nameIndex;
        cloneInfo.u2descriptorIndex = this.u2descriptorIndex;
        cloneInfo.resetRefCount();
        return cloneInfo;
    }

    /**
     * Return the name index.  
		 * @return the name index
     */
    protected int getNameIndex() {return u2nameIndex;}

    /**
     * Set the name index.
		 * @param index the index
     */
    protected void setNameIndex(int index) {u2nameIndex = index;}

    /**
     * Return the descriptor index.  
		 * @return the descriptor index
     */
    protected int getDescriptorIndex() {return u2descriptorIndex;}

    /**
     * Set the descriptor index.
		 * @param index the index
     */
    protected void setDescriptorIndex(int index) {u2descriptorIndex = index;}

    /** Check for Utf8 references to constant pool and mark them. */
    protected void markUtf8Refs(ConstantPool pool) 
    {
        pool.incRefCount(u2nameIndex);
        pool.incRefCount(u2descriptorIndex);
    }

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2nameIndex = din.readUnsignedShort();
        u2descriptorIndex = din.readUnsignedShort();
    }

    /** Write the 'info' data following the u1tag byte. */
    protected void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2nameIndex);
        dout.writeShort(u2descriptorIndex);
    }
}
