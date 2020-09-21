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
 * Representation of a field from a class-file.
 *
 * @author      Mark Welsh
 */
public class FieldInfo extends ClassItemInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------
    /**
     * Create a new FieldInfo from the file format data in the DataInput stream.
     *
     * @throws IOException if class file is corrupt or incomplete
     */
    public static FieldInfo create(DataInput din, ClassFile cf) throws java.io.IOException
    {
        if (din == null) throw new NullPointerException("No input stream was provided.");
        FieldInfo fi = new FieldInfo(cf);
        fi.read(din);
        return fi;
    }


    // Instance Methods ------------------------------------------------------
    protected FieldInfo(ClassFile cf) {super(cf);}
}
