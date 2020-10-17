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
 * Representation of an attribute.
 *
 * @author Mark Welsh
 */
public class LocalVariableTableAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2localVariableTableLength;
    private LocalVariableInfo[] localVariableTable;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Local variable table attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
// Instance Methods ------------------------------------------------------
    protected LocalVariableTableAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_LocalVariableTable;
    }

    /**
     * Return the array of local variable table entries.  @return the local variable info [ ]
     */
    protected LocalVariableInfo[] getLocalVariableTable() 
    {
        return localVariableTable;
    }

    /**
     * Sets local variable table.
     *
     * @param lvts the lvts
     */
    public void setLocalVariableTable(LocalVariableInfo[] lvts) {
      this.localVariableTable = lvts;
      this.u2localVariableTableLength = lvts.length;
      this.u4attrLength = 2 + 10 * u2localVariableTableLength;
    }

    /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
    protected void markUtf8RefsInInfo(ConstantPool pool) 
    {
        for (int i = 0; i < localVariableTable.length; i++)
        {
            localVariableTable[i].markUtf8Refs(pool);
        }
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2localVariableTableLength = din.readUnsignedShort();
        localVariableTable = new LocalVariableInfo[u2localVariableTableLength];
        for (int i = 0; i < u2localVariableTableLength; i++)
        {
            localVariableTable[i] = LocalVariableInfo.create(din);
        }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2localVariableTableLength);
        for (int i = 0; i < u2localVariableTableLength; i++)
        {
            localVariableTable[i].write(dout);
        }
    }

}

