/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

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
