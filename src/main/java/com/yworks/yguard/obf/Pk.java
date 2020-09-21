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
 * Tree item representing a package.
 *
 * @author      Mark Welsh
 */
public class Pk extends PkCl
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private Hashtable pks = new Hashtable(); // Owns a list of sub-package levels


    // Class Methods ---------------------------------------------------------
    /** Create the root entry for a tree. */
    public static Pk createRoot(ClassTree classTree) {return new Pk(classTree);}


    // Instance Methods ------------------------------------------------------
    /** Constructor for default package level. */
    public Pk(ClassTree classTree)
    {
        this(null, "");
        this.classTree = classTree;
    }

    /** Constructor for regular package levels. */
    public Pk(TreeItem parent, String name)
    {
        super(parent, name);
        if (parent == null && !name.equals(""))
        {
          throw new IllegalArgumentException("Internal error: only the default package has no parent");
        }
        else if (parent != null && name.equals(""))
        {
          throw new IllegalArgumentException("Internal error: the default package cannot have a parent");
        }
    }

    /** Get a package level by name. */
    public Pk getPackage(String name)  {return (Pk)pks.get(name);}

    /** Get an Enumeration of packages. */
    public Enumeration getPackageEnum()  {return pks.elements();}

    /** Return number of packages. */
    public int getPackageCount() {return pks.size();}

    /** Add a sub-package level. */
    public Pk addPackage(String name) 
    {
        Pk pk = getPackage(name);
        if (pk == null)
        {
            pk = new Pk(this, name);
            pks.put(name, pk);
        }
        return pk;
    }

    /** Add a class. */
    public Cl addClass(Object[] classInfo)
    {
        return addClass(false, classInfo);
    }

    /** Add a placeholder class. */
    public Cl addPlaceholderClass(String name) 
    {
        return addPlaceholderClass(false, name);
    }

    /** Generate unique obfuscated names for this namespace. */
    public void generateNames() 
    {
        super.generateNames();
        generateNames(pks);
    }
}

