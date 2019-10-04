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

