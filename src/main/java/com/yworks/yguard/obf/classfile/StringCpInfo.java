/*
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
 * Representation of a 'string' entry in the ConstantPool.
 *
 * @author Mark Welsh
 */
public class StringCpInfo extends CpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2stringIndex;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new String cp info.
     */
    protected StringCpInfo()
    {
        super(CONSTANT_String);
    }

    public int getStringIndex() {
        return u2stringIndex;
    }

    public void setStringIndex( final int stringIndex ) {
        u2stringIndex = stringIndex;
    }

    // Instance Methods ------------------------------------------------------
    /** Check for Utf8 references to constant pool and mark them. */
    protected void markUtf8Refs(ConstantPool pool) 
    {
        pool.incRefCount(u2stringIndex);
    }

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException 
    {
        u2stringIndex = din.readUnsignedShort();
    }

    /** Write the 'info' data following the u1tag byte. */
    protected void writeInfo(DataOutput dout) throws java.io.IOException 
    {
        dout.writeShort(u2stringIndex);
    }
}
