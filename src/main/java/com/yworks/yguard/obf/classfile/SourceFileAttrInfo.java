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

/**
 * Representation of an attribute.
 *
 * @author      Mark Welsh
 */
public class SourceFileAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2sourceFileIndex;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected SourceFileAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_SourceFile;
    }

    /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
    protected void markUtf8RefsInInfo(ConstantPool pool) 
    {
        pool.incRefCount(u2sourceFileIndex);
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2sourceFileIndex = din.readUnsignedShort();
    }
    
    protected void setSourceFileIndex(int index){
      this.u2sourceFileIndex = index;
    }
    
    protected int getSourceFileIndex(){
      return this.u2sourceFileIndex;
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2sourceFileIndex);
    }
}

