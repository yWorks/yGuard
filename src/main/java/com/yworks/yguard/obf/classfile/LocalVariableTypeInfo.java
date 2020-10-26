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
 * Representation of an Local Variable Type table entry.
 *
 * @author Mark Welsh
 */
public class LocalVariableTypeInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2startpc;
    private int u2length;
    private int u2nameIndex;
    private int u2signatureIndex;
    private int u2index;


    /**
     * Create local variable type info.
     *
     * 
		 * @param din the din
     * 
		 * @return the local variable type info
     * @throws IOException the io exception
     */
// Class Methods ---------------------------------------------------------
    public static LocalVariableTypeInfo create(DataInput din) throws java.io.IOException
    {
      if (din == null) throw new NullPointerException("DataInput cannot be null!");
        LocalVariableTypeInfo lvi = new LocalVariableTypeInfo();
        lvi.read(din);
        return lvi;
    }


    // Instance Methods ------------------------------------------------------
    private LocalVariableTypeInfo() {}

    /**
     * Return name index into Constant Pool.  
		 * @return the name index
     */
    protected int getNameIndex() {return u2nameIndex;}

    /**
     * Set the name index.  
		 * @param index the index
     */
    protected void setNameIndex(int index) {u2nameIndex = index;}

    /**
     * Return descriptor index into Constant Pool.  
		 * @return the signature index
     */
    protected int getSignatureIndex() {return u2signatureIndex;}

    /**
     * Set the descriptor index.  
		 * @param index the index
     */
    protected void setSignatureIndex(int index) {u2signatureIndex = index;}

    /**
     * Check for Utf8 references to constant pool and mark them.  
		 * @param pool the pool
     */
    protected void markUtf8Refs(ConstantPool pool)
    {
        pool.incRefCount(u2nameIndex);
        pool.incRefCount(u2signatureIndex);
    }

    private void read(DataInput din) throws java.io.IOException
    {
        u2startpc = din.readUnsignedShort();
        u2length = din.readUnsignedShort();
        u2nameIndex = din.readUnsignedShort();
        u2signatureIndex = din.readUnsignedShort();
        u2index = din.readUnsignedShort();
    }

    /**
     * Export the representation to a DataOutput stream.  
		 * @param dout the dout
     *
     * @throws IOException the io exception
     */
    public void write(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2startpc);
        dout.writeShort(u2length);
        dout.writeShort(u2nameIndex);
        dout.writeShort(u2signatureIndex);
        dout.writeShort(u2index);
    }
}
