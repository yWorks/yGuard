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
import java.lang.reflect.Modifier;

/**
 * Representation of an Inner Classes table entry.
 *
 * @author      Mark Welsh
 */
public class InnerClassesInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2innerClassInfoIndex;
    private int u2outerClassInfoIndex;
    private int u2innerNameIndex;
    private int u2innerClassAccessFlags;


    // Class Methods ---------------------------------------------------------
    public static InnerClassesInfo create(DataInput din) throws java.io.IOException
    {
        InnerClassesInfo ici = new InnerClassesInfo();
        ici.read(din);
        return ici;
    }
    
    public int getModifiers(){
      int mods = 0;
      if ((u2innerClassAccessFlags & 0x0001) == 0x0001) mods |= Modifier.PUBLIC;
      if ((u2innerClassAccessFlags & 0x0002) == 0x0002) mods |= Modifier.PRIVATE;
      if ((u2innerClassAccessFlags & 0x0004) == 0x0004) mods |= Modifier.PROTECTED;
      if ((u2innerClassAccessFlags & 0x0008) == 0x0008) mods |= Modifier.STATIC;
      if ((u2innerClassAccessFlags & 0x0010) == 0x0010) mods |= Modifier.FINAL;
      if ((u2innerClassAccessFlags & 0x0200) == 0x0200) mods |= Modifier.INTERFACE;
      if ((u2innerClassAccessFlags & 0x0400) == 0x0400) mods |= Modifier.ABSTRACT;
      return mods;
    }


    // Instance Methods ------------------------------------------------------
    private InnerClassesInfo() {}

    /** Return the inner class index. */
    protected int getInnerClassIndex() {return u2innerClassInfoIndex;}

    /** Return the name index. */
    protected int getInnerNameIndex() {return u2innerNameIndex;}

    /** Set the name index. */
    protected void setInnerNameIndex(int index) {u2innerNameIndex = index;}

    /** Check for Utf8 references to constant pool and mark them. */
    protected void markUtf8Refs(ConstantPool pool) 
    {
        // BUGFIX: a Swing1.1beta3 class has name index of zero - this is valid
        if (u2innerNameIndex != 0) 
        {
            pool.incRefCount(u2innerNameIndex);
        }
    }

    private void read(DataInput din) throws java.io.IOException
    {
        u2innerClassInfoIndex = din.readUnsignedShort();
        u2outerClassInfoIndex = din.readUnsignedShort();
        u2innerNameIndex = din.readUnsignedShort();
        u2innerClassAccessFlags = din.readUnsignedShort();
    }

    /** Export the representation to a DataOutput stream. */
    public void write(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2innerClassInfoIndex);
        dout.writeShort(u2outerClassInfoIndex);
        dout.writeShort(u2innerNameIndex);
        dout.writeShort(u2innerClassAccessFlags);
    }
}
