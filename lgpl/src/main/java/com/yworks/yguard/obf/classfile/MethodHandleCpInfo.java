package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'methodhandle' entry in the ConstantPool.
 *
 * @author      Sebastian Rheinnecker, yworks
 */
public class MethodHandleCpInfo extends CpInfo {
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u1referenceKind;
    private int u2referenceIndex;

    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected MethodHandleCpInfo()
    {
      super(CONSTANT_MethodHandle);
    }

    protected void readInfo(DataInput din) throws IOException {
      u1referenceKind = din.readUnsignedByte();
      u2referenceIndex = din.readUnsignedShort();
    }

    protected void writeInfo(DataOutput dout) throws IOException {
      dout.writeByte(u1referenceKind);
      dout.writeShort(u2referenceIndex);
    }

    protected int getReferenceKind() {
      return u1referenceKind;
    }

    protected int getReferenceIndex() {
      return u2referenceIndex;
    }
}
