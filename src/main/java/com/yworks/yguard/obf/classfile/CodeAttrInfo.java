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
import com.yworks.yguard.obf.*;

/**
 * Representation of an attribute.
 *
 * @author      Mark Welsh
 */
public class CodeAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------
    public static final int CONSTANT_FIELD_SIZE = 12;


    // Fields ----------------------------------------------------------------
    private int u2maxStack;
    private int u2maxLocals;
    private int u4codeLength;
    private byte[] code;
    private int u2exceptionTableLength;
    private ExceptionInfo[] exceptionTable;
    protected int u2attributesCount;
    protected AttrInfo[] attributes;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected CodeAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the length in bytes of the attribute. */
    protected int getAttrInfoLength() 
    {
        int length = CONSTANT_FIELD_SIZE + u4codeLength +
                        u2exceptionTableLength * ExceptionInfo.CONSTANT_FIELD_SIZE;
        for (int i = 0; i < u2attributesCount; i++)
        {
            length += AttrInfo.CONSTANT_FIELD_SIZE + attributes[i].getAttrInfoLength();
        }
        return length;
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_Code;
    }

    /**
     * Trim attributes from the classfile ('Code', 'Exceptions', 'ConstantValue'
     * are preserved, all others except the list in the String[] are killed).
     */
    protected void trimAttrsExcept(String[] keepAttrs) 
    {
        // Traverse all attributes, removing all except those on 'keep' list
        for (int i = 0; i < attributes.length; i++)
        {
            if (Tools.isInArray(attributes[i].getAttrName(), keepAttrs))
            {
                attributes[i].trimAttrsExcept(keepAttrs);
            }
            else
            {
                attributes[i] = null;
            }
        }

        // Delete the marked attributes
        AttrInfo[] left = new AttrInfo[attributes.length];
        int j = 0;
        for (int i = 0; i < attributes.length; i++)
        {
            if (attributes[i] != null)
            {
                left[j++] = attributes[i];
            }
        }
        attributes = new AttrInfo[j];
        System.arraycopy(left, 0, attributes, 0, j);
        u2attributesCount = j;
    }

    /** Check for references in the 'info' data to the constant pool and mark them. */
    protected void markUtf8RefsInInfo(ConstantPool pool) 
    {
        for (int i = 0; i < attributes.length; i++)
        {
            attributes[i].markUtf8Refs(pool);
        }
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2maxStack = din.readUnsignedShort();
        u2maxLocals = din.readUnsignedShort();
        u4codeLength = din.readInt();
        code = new byte[u4codeLength];
        din.readFully(code);
        u2exceptionTableLength = din.readUnsignedShort();
        exceptionTable = new ExceptionInfo[u2exceptionTableLength];
        for (int i = 0; i < u2exceptionTableLength; i++)
        {
            exceptionTable[i] = ExceptionInfo.create(din);
        }
        u2attributesCount = din.readUnsignedShort();
        attributes = new AttrInfo[u2attributesCount];
        for (int i = 0; i < u2attributesCount; i++)
        {
            attributes[i] = AttrInfo.create(din, owner);
        }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2maxStack);
        dout.writeShort(u2maxLocals);
        dout.writeInt(u4codeLength);
        dout.write(code);
        dout.writeShort(u2exceptionTableLength);
        for (int i = 0; i < u2exceptionTableLength; i++)
        {
            exceptionTable[i].write(dout);
        }
        dout.writeShort(u2attributesCount);
        for (int i = 0; i < u2attributesCount; i++)
        {
            attributes[i].write(dout);
        }
    }
}

