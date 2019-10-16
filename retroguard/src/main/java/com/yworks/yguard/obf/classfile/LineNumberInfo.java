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
 * Representation of an Line Number table entry.
 *
 * @author      Mark Welsh
 */
public class LineNumberInfo
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2startpc;
    private int u2lineNumber;

    public LineNumberInfo(int startPC, int lineNumber) {
        setLineNumber(lineNumber);
        setStartPC(startPC);
    }


  // Class Methods ---------------------------------------------------------
    public static LineNumberInfo create(DataInput din) throws java.io.IOException
    {
        LineNumberInfo lni = new LineNumberInfo();
        lni.read(din);
        return lni;
    }
    
    public void setLineNumber(int number){
      this.u2lineNumber = number;
    }
    
    public int getLineNumber(){
      return this.u2lineNumber;
    }
    
    public int getStartPC(){
      return this.u2startpc;
    }
    
    public void setStartPC(int startPc){
      this.u2startpc = startPc;
    }


    // Instance Methods ------------------------------------------------------
    public LineNumberInfo() {}
    private void read(DataInput din) throws java.io.IOException
    {
        u2startpc = din.readUnsignedShort();
        u2lineNumber = din.readUnsignedShort();
    }

    /** Export the representation to a DataOutput stream. */
    public void write(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2startpc);
        dout.writeShort(u2lineNumber);
    }
}
