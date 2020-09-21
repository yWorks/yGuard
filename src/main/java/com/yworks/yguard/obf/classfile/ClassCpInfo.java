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
 * Representation of a 'class' entry in the ConstantPool.
 *
 * @author      Mark Welsh
 */
public class ClassCpInfo extends AbstractTypeCpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected ClassCpInfo()
    {
        super(CONSTANT_Class);
    }

    /** Dump the content of the class file to the specified file (used for debugging). */
    public void dump(PrintWriter pw, ClassFile cf) 
    {
        pw.println("  Class: " + ((Utf8CpInfo)cf.getCpEntry(u2nameIndex)).getString());
    }
}
