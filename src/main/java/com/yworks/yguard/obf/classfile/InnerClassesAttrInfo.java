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
 * @author Mark Welsh
 */
public class InnerClassesAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2numberOfClasses;
    private InnerClassesInfo[] classes;


    // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Inner classes attr info.
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
// Instance Methods ------------------------------------------------------
    protected InnerClassesAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_InnerClasses;
    }

  /**
   * Return the array of inner classes data.
   *
   * @return the inner classes info [ ]
   */
  protected InnerClassesInfo[] getInfo()
    {
        return classes;
    }

    /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
    protected void markUtf8RefsInInfo(ConstantPool pool) 
    {
        for (int i = 0; i < classes.length; i++)
        {
            classes[i].markUtf8Refs(pool);
        }
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2numberOfClasses = din.readUnsignedShort();
        classes = new InnerClassesInfo[u2numberOfClasses];
        for (int i = 0; i < u2numberOfClasses; i++)
        {
            classes[i] = InnerClassesInfo.create(din);
        }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2numberOfClasses);
        for (int i = 0; i < u2numberOfClasses; i++)
        {
            classes[i].write(dout);
        }
    }
}

