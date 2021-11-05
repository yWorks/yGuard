/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

import java.io.*;

/**
 * Representation of an attribute.
 *
 * @author Mark Welsh
 */
public class ExceptionsAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2numberOfExceptions;
    private int[] u2exceptionIndexTable;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Exceptions attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
    protected ExceptionsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    // Instance Methods ------------------------------------------------------
    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_Exceptions;
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2numberOfExceptions = din.readUnsignedShort();
        u2exceptionIndexTable = new int[u2numberOfExceptions];
        for (int i = 0; i < u2numberOfExceptions; i++)
        {
            u2exceptionIndexTable[i] = din.readUnsignedShort();
        }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2numberOfExceptions);
        for (int i = 0; i < u2numberOfExceptions; i++)
        {
            dout.writeShort(u2exceptionIndexTable[i]);
        }
    }
}
