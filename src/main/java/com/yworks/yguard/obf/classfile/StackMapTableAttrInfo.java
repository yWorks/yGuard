package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Representation of an attribute.
 *
 */
public class StackMapTableAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2NumberOfEntries;
    private StackMapFrameInfo[] entries;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected StackMapTableAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName()
    {
        return ATTR_StackMapTable;
    }

    /** Return the array of local variable table entries. */
    protected StackMapFrameInfo[] getEntries()
    {
        return entries;
    }

    /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
    protected void markUtf8RefsInInfo(ConstantPool pool)
    {
        for (int i = 0; i < entries.length; i++)
        {
            entries[i].markUtf8Refs(pool);
        }
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2NumberOfEntries = din.readUnsignedShort();
        entries = new StackMapFrameInfo[u2NumberOfEntries];
        for (int i = 0; i < u2NumberOfEntries; i++)
        {
            entries[i] = StackMapFrameInfo.create(din);
        }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2NumberOfEntries);
        for (int i = 0; i < u2NumberOfEntries; i++)
        {
            entries[i].write(dout);
        }
    }

}
