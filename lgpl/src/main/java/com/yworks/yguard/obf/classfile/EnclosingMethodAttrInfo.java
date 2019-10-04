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
 * Representation of an attribute.
 *
 * @author      Mark Welsh
 */
public class EnclosingMethodAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2classIndex;
    private int u2nameAndTypeIndex;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected EnclosingMethodAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_EnclosingMethod;
    }

    /** Return the class index. */
    protected int getClassIndex() {return u2classIndex;}

    /** Return the class index. */
    protected void setClassIndex(int index) { this.u2classIndex = index; }

    /** Return the name-and-type index. */
    protected int getNameAndTypeIndex() {return u2nameAndTypeIndex;}

    /** Set the name-and-type index. */
    protected void setNameAndTypeIndex(int index) {u2nameAndTypeIndex = index;}

    /**
     * Check for Utf8 references in the 'info' data to the constant pool and
     * mark them; over-ride this in sub-classes.
     */
    protected void markUtf8RefsInInfo(ConstantPool pool) {
      //nothing to be done ClassCpInfo and NameAndTypeCpInfo are handled and marked 
      //automatically - their Utf8 references need no marking
      // however we mark the NameAndTypeCpInfo here, although this should be done in markNTRefs however
      // there is no markNTRefs for this class that might be called anywhere :-(
      if (u2nameAndTypeIndex > 0){
        NameAndTypeCpInfo ntcpi = (NameAndTypeCpInfo) pool.getCpEntry(u2nameAndTypeIndex);
        ntcpi.incRefCount();
      }
      // does not seem to be necessary, since all ClassCpInfo will be remapped equally, so
      // the reference counting is never used for ClassCpInfo...
//      ClassCpInfo cpi = (ClassCpInfo) pool.getCpEntry(u2classIndex);
//      cpi.incRefCount();
    }
    
    
    

    /** Read the 'info' data following the u1tag byte. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2classIndex = din.readUnsignedShort();
        u2nameAndTypeIndex = din.readUnsignedShort();
    }

    /** Write the 'info' data following the u1tag byte. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2classIndex);
        dout.writeShort(u2nameAndTypeIndex);
    }

    /** Dump the content of the class file to the specified file (used for debugging). */
    public void dump(PrintWriter pw, ClassFile cf, int index) 
    {
        pw.println("  EnclosingMethod " );
//        + Integer.toString(index) + ": " + ((Utf8CpInfo)cf.getCpEntry(((ClassCpInfo)cf.getCpEntry(u2classIndex)).getNameIndex())).getString() +
//                   " " + ((Utf8CpInfo)cf.getCpEntry(((NameAndTypeCpInfo)cf.getCpEntry(u2nameAndTypeIndex)).getNameIndex())).getString() +
//                   " " + ((Utf8CpInfo)cf.getCpEntry(((NameAndTypeCpInfo)cf.getCpEntry(u2nameAndTypeIndex)).getDescriptorIndex())).getString());
    }
}

