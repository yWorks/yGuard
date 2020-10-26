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
 * Representation of a 'integer' entry in the ConstantPool.
 *
 * @author Mark Welsh
 */
public class IntegerCpInfo extends CpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u4bytes;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Integer cp info.
     */
// Instance Methods ------------------------------------------------------
    protected IntegerCpInfo()
    {
        super(CONSTANT_Integer);
    }

    /**
     * As bool boolean.
     *
     * 
		 * @return the boolean
     */
    public boolean asBool(){
      return u4bytes != 0;
    }

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u4bytes = din.readInt();
    }

    /** Write the 'info' data following the u1tag byte. */
    protected void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeInt(u4bytes);
    }
}
