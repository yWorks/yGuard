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
 * Base to method and field tree items.
 *
 * @author      Mark Welsh
 */
abstract public class MdFd extends TreeItem
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private String descriptor = null;
    private ObfuscationConfig obfuscationConfig;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    /** Ctor. */
    public MdFd(TreeItem parent, boolean isSynthetic, String name, String descriptor, int access, ObfuscationConfig obfuscationConfig)
    {
        super(parent, name);
        this.descriptor = descriptor;
      this.obfuscationConfig = obfuscationConfig;
      this.access = access;

        this.isSynthetic = isSynthetic;
        if (name.equals("") || descriptor.equals("") || !(parent instanceof Cl))
        {
            System.err.println("Internal error: method/field must have name and descriptor, and have Class or Interface as parent");
        }

        // Disallow obfuscation of 'Synthetic' and native methods and fields
        if (isSynthetic || Modifier.isNative(access))
        {
            setOutName(getInName());
        }
    }

  public ObfuscationConfig getObfuscationConfig() {
    return obfuscationConfig;
  }

  /** Return the method or field descriptor String. */
    public String getDescriptor() {return descriptor;}

    /** Return the display name for field. */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        int modifiers = getModifiers();
        if (Modifier.isAbstract(modifiers))
        {
            sb.append("abstract ");
        }
        if (Modifier.isSynchronized(modifiers))
        {
            sb.append("synchronized ");
        }
        if (Modifier.isTransient(modifiers))
        {
            sb.append("transient ");
        }
        if (Modifier.isVolatile(modifiers))
        {
            sb.append("volatile ");
        }
        if (Modifier.isNative(modifiers))
        {
            sb.append("native ");
        }
        if (Modifier.isPublic(modifiers))
        {
            sb.append("public ");
        }
        if (Modifier.isProtected(modifiers))
        {
            sb.append("protected ");
        }
        if (Modifier.isPrivate(modifiers))
        {
            sb.append("private ");
        }
        if (Modifier.isStatic(modifiers))
        {
            sb.append("static ");
        }
        if (Modifier.isFinal(modifiers))
        {
            sb.append("final ");
        }
        sb.append(getReturnTypeName());
        sb.append(getInName());
        sb.append(getDescriptorName());
        return sb.toString();
    }

    /** Return the display name of the return type. */
    protected String getReturnTypeName()
    {
        String[] types = parseTypes();
        return (types.length > 0 ? types[types.length - 1] : "") + " ";
    }

    /** Return the display name of the descriptor types. */
    abstract protected String getDescriptorName();

    /** Return the parsed descriptor types array. */
    private String[] parsedTypes = null;
    protected String[] parseTypes()
    {
        if (parsedTypes == null)
        {
            try
            {
                parsedTypes = ClassFile.parseDescriptor(getDescriptor(), true);
            }
            catch (Exception e)
            {
                parsedTypes = null;
            }
        }
        return parsedTypes;
    }
}
