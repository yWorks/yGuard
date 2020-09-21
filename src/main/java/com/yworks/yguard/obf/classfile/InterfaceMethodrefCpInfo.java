/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 *

 *
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf.classfile;

import java.io.*;
import java.util.*;

/**
 * Representation of a 'interfacemethodref' entry in the ConstantPool.
 *
 * @author      Mark Welsh
 */
public class InterfaceMethodrefCpInfo extends RefCpInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected InterfaceMethodrefCpInfo()
    {
        super(CONSTANT_InterfaceMethodref);
    }
}
