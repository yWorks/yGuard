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
public class LocalVariableTypeTableAttrInfo extends AttrInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2localVariableTypeTableLength;
    private LocalVariableTypeInfo[] localVariableTypeTable;


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected LocalVariableTypeTableAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
        super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName() 
    {
        return ATTR_LocalVariableTypeTable;
    }

    /** Return the array of local variable table entries. */
    protected LocalVariableTypeInfo[] getLocalVariableTypeTable() 
    {
        return localVariableTypeTable;
    }
    public void setLocalVariableTypeTable(LocalVariableTypeInfo[] lvts) {
      this.localVariableTypeTable = lvts;
      this.u2localVariableTypeTableLength = lvts.length;
      this.u4attrLength = 2 + 10 * u2localVariableTypeTableLength;
    }

    /** Check for Utf8 references in the 'info' data to the constant pool and mark them. */
    protected void markUtf8RefsInInfo(ConstantPool pool) 
    {
        for (int i = 0; i < localVariableTypeTable.length; i++)
        {
            localVariableTypeTable[i].markUtf8Refs(pool);
        }
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws java.io.IOException
    {
        u2localVariableTypeTableLength = din.readUnsignedShort();
        localVariableTypeTable = new LocalVariableTypeInfo[u2localVariableTypeTableLength];
        for (int i = 0; i < u2localVariableTypeTableLength; i++)
        {
            localVariableTypeTable[i] = LocalVariableTypeInfo.create(din);
        }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2localVariableTypeTableLength);
        for (int i = 0; i < u2localVariableTypeTableLength; i++)
        {
            localVariableTypeTable[i].write(dout);
        }
    }

}

