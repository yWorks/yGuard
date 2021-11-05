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
 * Representation of a 'ref'-type entry in the ConstantPool.
 *
 * @author Mark Welsh
 */
abstract public class RefCpInfo extends CpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2classIndex;
    private int u2nameAndTypeIndex;


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Ref cp info.
     *
     * @param tag the tag
     */
    protected RefCpInfo(int tag)
    {
        super(tag);
    }

    // Instance Methods ------------------------------------------------------
    /**
     * Return the class index.
     *
     * @return the class index
     */
    protected int getClassIndex() {return u2classIndex;}

    /**
     * Return the name-and-type index.
     *
     * @return the name and type index
     */
    protected int getNameAndTypeIndex() {return u2nameAndTypeIndex;}

    /**
     * Set the name-and-type index.
     *
     * @param index the index
     */
    protected void setNameAndTypeIndex(int index) {u2nameAndTypeIndex = index;}

    /** Check for N+T references to constant pool and mark them. */
    protected void markNTRefs(ConstantPool pool) 
    {
        pool.incRefCount(u2nameAndTypeIndex);
    }

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2classIndex = din.readUnsignedShort();
        u2nameAndTypeIndex = din.readUnsignedShort();
    }

    /** Write the 'info' data following the u1tag byte. */
    protected void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2classIndex);
        dout.writeShort(u2nameAndTypeIndex);
    }

    /** Dump the content of the class file to the specified file (used for debugging). */
    public void dump(PrintWriter pw, ClassFile cf, int index) 
    {
        pw.println("  Ref " + Integer.toString(index) + ": " + ((Utf8CpInfo)cf.getCpEntry(((ClassCpInfo)cf.getCpEntry(u2classIndex)).getNameIndex())).getString() +
                   " " + ((Utf8CpInfo)cf.getCpEntry(((NameAndTypeCpInfo)cf.getCpEntry(u2nameAndTypeIndex)).getNameIndex())).getString() +
                   " " + ((Utf8CpInfo)cf.getCpEntry(((NameAndTypeCpInfo)cf.getCpEntry(u2nameAndTypeIndex)).getDescriptorIndex())).getString());
    }
}
