/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'interfacemethodref' entry in the ConstantPool.
 *
 * @author Mark Welsh
 */
public class InterfaceMethodrefCpInfo extends RefCpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Interface methodref cp info.
     */
    protected InterfaceMethodrefCpInfo()
    {
        super(CONSTANT_InterfaceMethodref);
    }

    // Instance Methods ------------------------------------------------------
}
