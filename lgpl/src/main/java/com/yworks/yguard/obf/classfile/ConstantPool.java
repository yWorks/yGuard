/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author may be contacted at yguard@yworks.com 
 *
 * Java and all Java-based marks are trademarks or registered 
 * trademarks of Sun Microsystems, Inc. in the U.S. and other countries.
 */
package com.yworks.yguard.obf.classfile;

import java.io.*;
import java.util.*;

/**
 * A representation of the data in a Java class-file's Constant Pool.
 * Constant Pool entries are managed by reference counting.
 *
 * @author      Mark Welsh
 */
public class ConstantPool
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private ClassFile myClassFile;
    private Vector pool;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    /** Ctor, which initializes Constant Pool using an array of CpInfo. */
    public ConstantPool(ClassFile classFile, CpInfo[] cpInfo) 
    {
        myClassFile = classFile;
        int length = cpInfo.length;
        pool = new Vector(length);
        pool.setSize(length);
        for (int i = 0; i < length; i++)
        {
            pool.setElementAt(cpInfo[i], i);
        }
    }

    /** Return an Enumeration of all Constant Pool entries. */
    public Enumeration elements()
    {
        return pool.elements();
    }

    /** Return the Constant Pool length. */
    public int length()
    {
        return pool.size();
    }

    /** Return the specified Constant Pool entry. */
    public CpInfo getCpEntry(int i) 
    {
        if (i < pool.size())
        {
            return (CpInfo)pool.elementAt(i);
        }
        throw new IndexOutOfBoundsException("Constant Pool index out of range.");
    }

    /** Set the reference count for each element, using references from the owning ClassFile. */
    public void updateRefCount() 
    {
        // Reset all reference counts to zero
        walkPool(new PoolAction() {
            public void defaultAction(CpInfo cpInfo)  {cpInfo.resetRefCount();}
        });

        // Count the direct references to Utf8 entries
        myClassFile.markUtf8Refs(this);

        // Count the direct references to NameAndType entries
        myClassFile.markNTRefs(this);

        // Go through pool, clearing the Utf8 entries which have no references
        walkPool(new PoolAction() {
            public void utf8Action(Utf8CpInfo cpInfo)  {if (cpInfo.getRefCount() == 0) cpInfo.clearString();}
        });
    }

    /** Increment the reference count for the specified element. */
    public void incRefCount(int i) 
    {
        CpInfo cpInfo = (CpInfo)pool.elementAt(i);
        if (cpInfo == null)
        {
            // This can happen for JDK1.2 code so remove - 981123
            //throw new Exception("Illegal access to a Constant Pool element.");
        }
        else
        {
            cpInfo.incRefCount();
        }
    }

    /** Remap a specified Utf8 entry to the given value and return its new index. */
    public int remapUtf8To(String newString, int oldIndex) 
    {
        decRefCount(oldIndex);
        return addUtf8Entry(newString);
    }

    /** Decrement the reference count for the specified element, blanking if Utf and refs are zero. */
    public void decRefCount(int i) 
    {
        CpInfo cpInfo = (CpInfo)pool.elementAt(i);
        if (cpInfo == null)
        {
            // This can happen for JDK1.2 code so remove - 981123
            //throw new Exception("Illegal access to a Constant Pool element.");
        }
        else
        {
            cpInfo.decRefCount();
        }
    }

    /** Add an entry to the constant pool and return its index. */
    public int addEntry(CpInfo entry) 
    {
        int oldLength = pool.size();
        pool.setSize(oldLength + 1);
        pool.setElementAt(entry, oldLength);
        return oldLength;
    }

    // Add a string to the constant pool and return its index
    private int addUtf8Entry(String s) 
    {
        // Search pool for the string. If found, just increment the reference count and return the index
        for (int i = 0; i < pool.size(); i++)
        {
            Object o = pool.elementAt(i);
            if (o instanceof Utf8CpInfo)
            {
                Utf8CpInfo entry = (Utf8CpInfo)o;
                if (entry.getString().equals(s))
                {
                    entry.incRefCount();
                    return i;
                }
            }
        }

        // No luck, so try to overwrite an old, blanked entry
        for (int i = 0; i < pool.size(); i++)
        {
            Object o = pool.elementAt(i);
            if (o instanceof Utf8CpInfo)
            {
                Utf8CpInfo entry = (Utf8CpInfo)o;
                if (entry.getRefCount() == 0)
                {
                    entry.setString(s);
                    entry.incRefCount();
                    return i;
                }
            }
        }

        // Still no luck, so append a fresh Utf8CpInfo entry to the pool
        return addEntry(new Utf8CpInfo(s));
    }

    // Data walker
    class PoolAction {public void utf8Action(Utf8CpInfo cpInfo)  {defaultAction(cpInfo);}
                      public void defaultAction(CpInfo cpInfo)  {}}
    private void walkPool(PoolAction pa) 
    {
        for (Enumeration enumeration = pool.elements(); enumeration.hasMoreElements(); )
        {
            Object o = enumeration.nextElement();
            if (o instanceof Utf8CpInfo)
            {
                pa.utf8Action((Utf8CpInfo)o);
            }
            else if (o instanceof CpInfo)
            {
                pa.defaultAction((CpInfo)o);
            }
        }
    }
}
