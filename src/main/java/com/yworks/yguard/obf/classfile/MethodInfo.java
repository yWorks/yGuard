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
 * Representation of a method from a class-file.
 *
 * @author Mark Welsh
 */
public class MethodInfo extends ClassItemInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------

    /**
     * Create a new MethodInfo from the file format data in the DataInput stream.
     *
     * @param din the din
     * @param cf  the cf
     * @return the method info
     * @throws IOException if class file is corrupt or incomplete
     */
    public static MethodInfo create(DataInput din, ClassFile cf) throws java.io.IOException
    {
        if (din == null) throw new NullPointerException("No input stream was provided.");
        MethodInfo mi = new MethodInfo(cf);
        mi.read(din);
        return mi;
    }


    /**
     * Instantiates a new Method info.
     *
     * @param cf the cf
     */
// Instance Methods ------------------------------------------------------
    protected MethodInfo(ClassFile cf) {super(cf);}
}
