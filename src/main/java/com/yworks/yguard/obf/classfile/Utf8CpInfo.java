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
import java.io.UnsupportedEncodingException;

/**
 * Representation of a 'UTF8' entry in the ConstantPool.
 *
 * @author      Mark Welsh
 */
public class Utf8CpInfo extends CpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2length;
    private byte[] bytes;
    private String utf8string;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected Utf8CpInfo()
    {
        super(CONSTANT_Utf8);
    }

    /** Ctor used when appending fresh Utf8 entries to the constant pool. */
    public Utf8CpInfo(String s) 
    {
        super(CONSTANT_Utf8);
        setString(s);
        refCount = 1;
    }

    /** Decrement the reference count, blanking the entry if no more references. */
    public void decRefCount() 
    {
        super.decRefCount();
        if (refCount == 0)
        {
            clearString();
        }
    }

    /** Return UTF8 data as a String. */
    public String getString() 
    {
        if (utf8string == null)
        {
          try{
            utf8string = new String(bytes, "UTF8");
          } catch (UnsupportedEncodingException uee){
            throw new RuntimeException("Could not decode UTF8");
          }
        }
        return utf8string;
    }

    /** Set UTF8 data as String. */
    public void setString(String str) 
    {
        utf8string = str;
        try{
          bytes = str.getBytes("UTF8");
        } catch (UnsupportedEncodingException uee){
          throw new RuntimeException("Could not encode UTF8");
        }
        u2length = bytes.length;
    }

    /** Set the UTF8 data to empty. */
    public void clearString() 
    {
        u2length = 0;
        bytes = new byte[0];
        utf8string = null;
        getString();
    }

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2length = din.readUnsignedShort();
        bytes = new byte[u2length];
        din.readFully(bytes);
        getString();
    }

    /** Write the 'info' data following the u1tag byte. */
    protected void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2length);
	if (bytes.length > 0) {
	    dout.write(bytes);
	}
    }
}
