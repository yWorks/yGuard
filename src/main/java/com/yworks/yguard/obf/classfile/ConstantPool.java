/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf.classfile;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A representation of the data in a Java class-file's Constant Pool.
 * Constant Pool entries are managed by reference counting.
 *
 * @author Mark Welsh
 */
public class ConstantPool {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private final ClassFile myClassFile;
  private final Vector pool;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------

  /**
   * Ctor, which initializes Constant Pool using an array of CpInfo.
   *
   * @param classFile the class file
   * @param cpInfo    the cp info
   */
  public ConstantPool( ClassFile classFile, CpInfo[] cpInfo ) {
    myClassFile = classFile;
    int length = cpInfo.length;
    pool = new Vector(length);
    pool.setSize(length);
    for (int i = 0; i < length; i++) {
      pool.setElementAt(cpInfo[i], i);
    }
  }

  /**
   * Return an Enumeration of all Constant Pool entries.
   *
   * @return the enumeration
   */
  public Enumeration elements() {
    return pool.elements();
  }

  /**
   * Return the Constant Pool length.
   *
   * @return the int
   */
  public int length() {
    return pool.size();
  }

  /**
   * Return the specified Constant Pool entry.
   *
   * @param i the
   * @return the cp entry
   */
  public CpInfo getCpEntry( int i ) {
    if (i < pool.size()) {
      return (CpInfo) pool.elementAt(i);
    }
    throw new IndexOutOfBoundsException("Constant Pool index out of range.");
  }

  /**
   * Set the reference count for each element, using references from the owning ClassFile.
   */
  public void updateRefCount() {
    // Reset all reference counts to zero
    walkPool(new PoolAction() {
      public void defaultAction( CpInfo cpInfo ) {
        cpInfo.resetRefCount();
      }
    });

    // Count the direct references to Utf8 entries
    myClassFile.markUtf8Refs(this);

    // Count the direct references to NameAndType entries
    myClassFile.markNTRefs(this);

    // Go through pool, clearing the Utf8 entries which have no references
    walkPool(new PoolAction() {
      public void utf8Action( Utf8CpInfo cpInfo ) {
        if (cpInfo.getRefCount() == 0) {
          cpInfo.clearString();
        }
      }
    });
  }

  /**
   * Increment the reference count for the specified element.
   *
   * @param i the
   */
  public void incRefCount( int i ) {
    CpInfo cpInfo = (CpInfo) pool.elementAt(i);
    if (cpInfo == null) {
      // This can happen for JDK1.2 code so remove - 981123
      //throw new Exception("Illegal access to a Constant Pool element.");
    } else {
      cpInfo.incRefCount();
    }
  }

  /**
   * Remap a specified Utf8 entry to the given value and return its new index.
   *
   * @param newString the new string
   * @param oldIndex  the old index
   * @return the int
   */
  public int remapUtf8To( String newString, int oldIndex ) {
    decRefCount(oldIndex);
    return addUtf8Entry(newString);
  }

  /**
   * Decrement the reference count for the specified element, blanking if Utf and refs are zero.
   *
   * @param i the
   */
  public void decRefCount( int i ) {
    CpInfo cpInfo = (CpInfo) pool.elementAt(i);
    if (cpInfo == null) {
      // This can happen for JDK1.2 code so remove - 981123
      //throw new Exception("Illegal access to a Constant Pool element.");
    } else {
      cpInfo.decRefCount();
    }
  }

  /**
   * Add an entry to the constant pool and return its index.
   *
   * @param entry the entry
   * @return the int
   */
  public int addEntry( CpInfo entry ) {
    int oldLength = pool.size();
    pool.setSize(oldLength + 1);
    pool.setElementAt(entry, oldLength);
    return oldLength;
  }

  // Add a string to the constant pool and return its index
  private int addUtf8Entry( String s ) {
    // Search pool for the string. If found, just increment the reference count and return the index
    for (int i = 0; i < pool.size(); i++) {
      Object o = pool.elementAt(i);
      if (o instanceof Utf8CpInfo) {
        Utf8CpInfo entry = (Utf8CpInfo) o;
        if (entry.getString().equals(s)) {
          entry.incRefCount();
          return i;
        }
      }
    }

    // No luck, so try to overwrite an old, blanked entry
    for (int i = 0; i < pool.size(); i++) {
      Object o = pool.elementAt(i);
      if (o instanceof Utf8CpInfo) {
        Utf8CpInfo entry = (Utf8CpInfo) o;
        if (entry.getRefCount() == 0) {
          entry.setString(s);
          entry.incRefCount();
          return i;
        }
      }
    }

    // Still no luck, so append a fresh Utf8CpInfo entry to the pool
    return addEntry(new Utf8CpInfo(s));
  }

  /**
   * The type Pool action.
   */
// Data walker
  class PoolAction {
    /**
     * Utf 8 action.
     *
     * @param cpInfo the cp info
     */
    public void utf8Action( Utf8CpInfo cpInfo ) {
      defaultAction(cpInfo);
    }

    /**
     * Default action.
     *
     * @param cpInfo the cp info
     */
    public void defaultAction( CpInfo cpInfo ) {
    }
  }

  private void walkPool( PoolAction pa ) {
    for (Enumeration enumeration = pool.elements(); enumeration.hasMoreElements(); ) {
      Object o = enumeration.nextElement();
      if (o instanceof Utf8CpInfo) {
        pa.utf8Action((Utf8CpInfo) o);
      } else if (o instanceof CpInfo) {
        pa.defaultAction((CpInfo) o);
      }
    }
  }
}
