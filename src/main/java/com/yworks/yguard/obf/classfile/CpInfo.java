/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard.obf.classfile;

import java.io.*;
import java.util.*;

/**
 * Representation of an entry in the ConstantPool. Specific types of entry
 * have their representations sub-classed from this.
 *
 * @author      Mark Welsh
 */
abstract public class CpInfo implements ClassConstants
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u1tag;
    private byte info[];

    protected int refCount = 0;  // Used for reference counting in Constant Pool


    // Class Methods ---------------------------------------------------------
    /**
     * Create a new CpInfo from the data passed.
     *
     * @throws IOException if class file is corrupt or incomplete
     */
    public static CpInfo create(DataInput din) throws java.io.IOException
    {
        if (din == null) throw new NullPointerException("No input stream was provided.");

        // Instantiate based on tag byte
        CpInfo ci = null;
        switch (din.readUnsignedByte())
        {
        case CONSTANT_Utf8:                 ci = new Utf8CpInfo();              break;
        case CONSTANT_Integer:              ci = new IntegerCpInfo();           break;
        case CONSTANT_Float:                ci = new FloatCpInfo();             break;
        case CONSTANT_Long:                 ci = new LongCpInfo();              break;
        case CONSTANT_Double:               ci = new DoubleCpInfo();            break;
        case CONSTANT_Class:                ci = new ClassCpInfo();             break;
        case CONSTANT_String:               ci = new StringCpInfo();            break;
        case CONSTANT_Fieldref:             ci = new FieldrefCpInfo();          break;
        case CONSTANT_Methodref:            ci = new MethodrefCpInfo();         break;
        case CONSTANT_InterfaceMethodref:   ci = new InterfaceMethodrefCpInfo();break;
        case CONSTANT_NameAndType:          ci = new NameAndTypeCpInfo();       break;
        case CONSTANT_MethodHandle:         ci = new MethodHandleCpInfo();      break;
        case CONSTANT_MethodType:           ci = new MethodTypeCpInfo();        break;
        case CONSTANT_Dynamic:              ci = new DynamicCpInfo();           break;
        case CONSTANT_InvokeDynamic:        ci = new InvokeDynamicCpInfo();     break;
        case CONSTANT_Module:               ci = new ModuleCpInfo();            break;
        case CONSTANT_Package:              ci = new PackageCpInfo();           break;
        default:    throw new IOException("Unknown tag type in constant pool.");
        }
        ci.readInfo(din);
        return ci;
    }


    // Instance Methods ------------------------------------------------------
    protected CpInfo(int tag)
    {
        u1tag = tag;
    }

    /** Read the 'info' data following the u1tag byte; over-ride this in sub-classes. */
    abstract protected void readInfo(DataInput din) throws java.io.IOException;

    /** Check for Utf8 references to constant pool and mark them; over-ride this in sub-classes. */
    protected void markUtf8Refs(ConstantPool pool)  {}

    /** Check for NameAndType references to constant pool and mark them; over-ride this in sub-classes. */
    protected void markNTRefs(ConstantPool pool)  {}

    /** Export the representation to a DataOutput stream. */
    public void write(DataOutput dout) throws java.io.IOException
    {
        if (dout == null) throw new IOException("No output stream was provided.");
        dout.writeByte(u1tag);
        writeInfo(dout);
    }

    /** Write the 'info' data following the u1tag byte; over-ride this in sub-classes. */
    abstract protected void writeInfo(DataOutput dout) throws java.io.IOException;

    /** Return the reference count. */
    public int getRefCount() {return refCount;}

    /** Increment the reference count. */
    public void incRefCount() {refCount++;}

    /** Decrement the reference count. */
    public void decRefCount() 
    {
        if (refCount == 0) {
            throw new IllegalStateException("Illegal to decrement reference count that is already zero.");
        }
        refCount--;
    }

    /** Reset the reference count to zero. */
    public void resetRefCount() {refCount = 0;}

    /** Dump the content of the class file to the specified file (used for debugging). */
    public void dump(PrintWriter pw, ClassFile cf, int index)  {
      pw.println(this.getClass().getName());
    }
}
