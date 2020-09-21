/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard.obf;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import com.yworks.yguard.obf.classfile.*;

/**
 * Tree item representing a method.
 *
 * @author      Mark Welsh
 */
public class Md extends MdFd
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    /** Ctor. */
    public Md(TreeItem parent, boolean isSynthetic, String name, String descriptor,
              int access, ObfuscationConfig obfuscationConfig)
    {
        super(parent, isSynthetic, name, descriptor, access, obfuscationConfig);
    }

    /** Return the display name of the descriptor types. */
    protected String getDescriptorName()
    {
        String[] types = parseTypes();
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        if (types.length > 0)
        {
            for (int i = 0; i < types.length - 1; i++)
            {
                sb.append(types[i]);
                if (i < types.length - 2)
                {
                    sb.append(", ");
                }
            }
        }
        sb.append(");");
        return sb.toString();
    }

    /** Are this method's name/descriptor a match to the wildcard patterns? */
    public boolean isWildcardMatch(String namePattern, String descPattern) {
        return 
            isMatch(namePattern, getFullInName()) &&
            isMatch(descPattern, getDescriptor());
    }

    /** Are this method's name/descriptor a non-recursive match 
        to the wildcard patterns? */
    public boolean isNRWildcardMatch(String namePattern, String descPattern) {
        return 
            isNRMatch(namePattern, getFullInName()) &&
            isMatch(descPattern, getDescriptor());
    }
}

