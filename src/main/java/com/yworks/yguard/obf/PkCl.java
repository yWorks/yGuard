/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard.obf;

import com.yworks.yguard.obf.classfile.AttrInfo;
import com.yworks.yguard.obf.classfile.Logger;
import com.yworks.yguard.Conversion;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Base to package and class tree item.
 *
 * @author Mark Welsh
 */
abstract public class PkCl extends TreeItem
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    /**
     * Owns a list of classes.
     */
    protected Hashtable cls = new Hashtable();


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------

    /**
     * Ctor.  @param parent the parent
     *
     * @param name the name
     */
    public PkCl(TreeItem parent, String name)
    {
        super(parent, name);
    }

    /**
     * Get a class by name.  @param name the name
     *
     * @return the class
     */
    public Cl getClass(String name)  {return (Cl)cls.get(name);}

    /**
     * Get an Enumeration of classes directly beneath this PkCl.  @return the class enum
     */
    public Enumeration getClassEnum() {return cls.elements();}

    /**
     * Get an Enumeration of all classes (outer and inner) in the tree beneath this PkCl.  @return the all class enum
     */
    public Enumeration getAllClassEnum()
    {
        Vector allClasses = new Vector();
        addAllClasses(allClasses);
        return allClasses.elements();
    }

    /**
     * List classes and recursively compose a list of all inner classes.  @param allClasses the all classes
     */
    protected void addAllClasses(Vector allClasses)
    {
        for (Enumeration enumeration = cls.elements(); enumeration.hasMoreElements(); )
        {
            Cl cl = (Cl)enumeration.nextElement();
            allClasses.addElement(cl);
            cl.addAllClasses(allClasses);
        }
    }

    /**
     * Return number of classes.  @return the class count
     */
    public int getClassCount() {return cls.size();}

    /**
     * Add a class to the list of owned classes.  @param classInfo the class info
     *
     * @return the cl
     */
    abstract public Cl addClass(Object[] classInfo) ;


    /**
     * Add a class to the list of owned classes.  @param isInnerClass the is inner class
     *
     * @param classInfo the class info
     * @return the cl
     */
    public Cl addClass(boolean isInnerClass, Object[] classInfo)
    {
      String name = (String) classInfo[0];
      String superName = (String) classInfo[1];
      String[] interfaceNames = (String[]) classInfo[2];
      int modifiers = (Integer) classInfo[3];
      ObfuscationConfig obfuscationConfig = (ObfuscationConfig) classInfo[4];
        Cl cl = getClass(name);

        // Remove placeholder if present
        PlaceholderCl plClassItem = null;
        if (cl instanceof PlaceholderCl)
        {
            plClassItem = (PlaceholderCl)cl;
            cls.remove(name);
            cl = null;
        }

        // Add the class, if not already present
        if (cl == null)
        {
            cl = new Cl(this, isInnerClass, name, superName, interfaceNames, modifiers, obfuscationConfig);
            cls.put(name, cl);
        }

        // Copy over the inner class data from the placeholder, if any
        if (plClassItem != null)
        {
            for (Enumeration enumeration = plClassItem.getClassEnum(); enumeration.hasMoreElements(); )
            {
                Cl innerCl = (Cl)enumeration.nextElement();
                innerCl.setParent(cl);
                cl.addClass(innerCl);
            }
        }
        return cl;
    }

    /**
     * Add a placeholder class to our list of owned classes, to be replaced later by the full class.  @param name the name
     *
     * @return the cl
     */
    abstract public Cl addPlaceholderClass(String name) ;

    /**
     * Add a placeholder class to our list of owned classes, to be replaced later by the full class.  @param isInnerClass the is inner class
     *
     * @param name the name
     * @return the cl
     */
    public Cl addPlaceholderClass(boolean isInnerClass, String name)
    {
        Cl cl = getClass(name);
        if (cl == null)
        {
            cl = new PlaceholderCl(this, isInnerClass, name);
            cls.put(name, cl);
        }
        return cl;
    }

    /**
     * Generate unique obfuscated names for this namespace.
     */
    public void generateNames()
    {
        generateNames(cls);
    }

    /**
     * Generate unique obfuscated names for a given namespace.  @param hash the hash
     */
    protected void generateNames(Hashtable hash)
    {
        Vector vec = new Vector();
        for (Enumeration enumeration = hash.elements(); enumeration.hasMoreElements(); )
        {
            TreeItem ti = (TreeItem)enumeration.nextElement();
            if (ti.isFixed())
            {
                vec.addElement(ti.getOutName());
            }
        }
        String[] noObfNames = new String[vec.size()];
        for (int i = 0; i < noObfNames.length; i++)
        {
            noObfNames[i] = (String)vec.elementAt(i);
        }
        NameMakerFactory nmf = NameMakerFactory.getInstance();
        for (Enumeration enumeration = hash.elements(); enumeration.hasMoreElements(); )
        {
            TreeItem ti = (TreeItem)enumeration.nextElement();
            if (!ti.isFixed())
            {
                if (ti instanceof Cl && ((Cl)ti).isInnerClass()){
                  NameMaker innerClassNameMaker = nmf.getInnerClassNameMaker(noObfNames, getFullInName());
                  ti.setOutName(innerClassNameMaker.nextName(null));
                } else if (ti instanceof Pk){
                  NameMaker packageNameMaker = nmf.getPackageNameMaker(noObfNames, getFullInName());
                  ti.setOutName(packageNameMaker.nextName(null));
                } else {
                  // package-info.class package annotation
                  if ("package-info".equals(ti.getInName())) {
                    ti.setOutName("package-info");
                  } else if ("module-info".equals(ti.getInName())) {
                    ti.setOutName("module-info");
                  } else {
                    NameMaker classNameMaker = nmf.getClassNameMaker(noObfNames, getFullInName());
                    boolean newNameFound = true;
                    Cl.ClassResolver resolver = Cl.getClassResolver();
                    do {
                      ti.setOutName(classNameMaker.nextName(null));
                      String newName = ti.getFullOutName();
                      try{
                        resolver.resolve(Conversion.toJavaClass(newName));
                        newNameFound = false;
                      } catch (ClassNotFoundException cnfe){
                        newNameFound = true;
                      }
                    } while (!newNameFound);
                  }
                }
            }
        }
    }
}

