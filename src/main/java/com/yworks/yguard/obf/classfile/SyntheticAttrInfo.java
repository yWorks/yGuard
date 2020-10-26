/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Representation of an attribute.
 *
 * @author Mark Welsh
 */
public class SyntheticAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Synthetic attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
// Instance Methods ------------------------------------------------------
    protected SyntheticAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute. */
    protected String getAttrName() 
    {
        return ATTR_Synthetic;
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din)  {}

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout)  {}
}

