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
 * Representation of an Exception table entry.
 *
 * @author Mark Welsh
 */
public class ExceptionInfo
{
  /**
   * The constant CONSTANT_FIELD_SIZE.
   */
// Constants -------------------------------------------------------------
    public static final int CONSTANT_FIELD_SIZE = 8;


    // Fields ----------------------------------------------------------------
    private int u2startpc;
    private int u2endpc;
    private int u2handlerpc;
    private int u2catchType;


  /**
   * Create exception info.
   *
   * @param din the din
   * @return the exception info
   * @throws IOException the io exception
   */
// Class Methods ---------------------------------------------------------
    public static ExceptionInfo create(DataInput din) throws java.io.IOException
    {
        ExceptionInfo ei = new ExceptionInfo();
        ei.read(din);
        return ei;
    }


    // Instance Methods ------------------------------------------------------
    private ExceptionInfo() {}
    private void read(DataInput din) throws java.io.IOException
    {
        u2startpc = din.readUnsignedShort();
        u2endpc = din.readUnsignedShort();
        u2handlerpc = din.readUnsignedShort();
        u2catchType = din.readUnsignedShort();
    }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write(DataOutput dout) throws java.io.IOException
    {
        dout.writeShort(u2startpc);
        dout.writeShort(u2endpc);
        dout.writeShort(u2handlerpc);
        dout.writeShort(u2catchType);
    }
}
