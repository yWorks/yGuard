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
 * Representation of an attribute.
 *
 * @author Mark Welsh
 */
public class CodeAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------
    /**
     * The constant CONSTANT_FIELD_SIZE.
     */
    public static final int CONSTANT_FIELD_SIZE = 12;


    // Fields ----------------------------------------------------------------
    private int u2maxStack;
    private int u2maxLocals;
    private int u4codeLength;
    private byte[] code;
    private int u2exceptionTableLength;
    private ExceptionInfo[] exceptionTable;
    /**
     * The attributes count.
     */
    protected int u2attributesCount;
    /**
     * The Attributes.
     */
    protected AttrInfo[] attributes;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Code attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
    protected CodeAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    // Instance Methods ------------------------------------------------------
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
      attributes = AttrInfo.filter(attributes, keepAttrs);
      u2attributesCount = attributes.length;
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
