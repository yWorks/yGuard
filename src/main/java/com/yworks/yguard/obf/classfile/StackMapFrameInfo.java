package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Representation of an Local Variable table entry.
 *
 * @author Mark Welsh
 */
public class StackMapFrameInfo {
  private VerificationTypeInfo[] verificationTypeInfoStack;
  private int u2_offset_delta;
  private VerificationTypeInfo[] verificationTypeInfoLocals;
  private int u1_frameType;
  // Constants -------------------------------------------------------------


  /**
   * Create stack map frame info.
   *
   * @param din the din
   * @return the stack map frame info
   * @throws IOException the io exception
   */
// Class Methods ---------------------------------------------------------
    public static StackMapFrameInfo create(DataInput din) throws java.io.IOException
    {
      if (din == null) throw new NullPointerException("DataInput cannot be null!");
        StackMapFrameInfo smfi = new StackMapFrameInfo();
        smfi.read(din);
        return smfi;
    }


    // Instance Methods ------------------------------------------------------
    private StackMapFrameInfo() {}

  /**
   * Check for Utf8 references to constant pool and mark them.
   *
   * @param pool the pool
   */
  protected void markUtf8Refs(ConstantPool pool)
    {
      if (u1_frameType < 64){
        // SAME
      } else if (u1_frameType >= 64 && u1_frameType <= 127){
        // SAME_LOCALS_1_STACK_ITEM;
        verificationTypeInfoStack[0].markUtf8Refs(pool);
      } else if (u1_frameType == 247){
        // SAME_LOCALS_1_STACK_ITEM_EXTENDED
        verificationTypeInfoStack[0].markUtf8Refs(pool);
      } else if (u1_frameType >= 248 && u1_frameType <= 250){
        // CHOP
      } else if (u1_frameType == 251){
        // SAME_FRAME_EXTENDED
      } else if (u1_frameType >= 252 && u1_frameType <= 254){
        // APPEND
        for (int i = 0; i < verificationTypeInfoStack.length; i++) {
          verificationTypeInfoStack[i].markUtf8Refs(pool);
        }
      } else if (u1_frameType == 255){
        // FULL_FRAME
        for (int i = 0; i < verificationTypeInfoLocals.length; i++) {
          verificationTypeInfoLocals[i].markUtf8Refs(pool);
        }
        for (int i = 0; i < verificationTypeInfoStack.length; i++) {
          verificationTypeInfoStack[i].markUtf8Refs(pool);
        }
      } else {
        throw new IllegalArgumentException("Unknown frame type " + u1_frameType);
      }
    }


  /**
   * Get verification type infos collection.
   *
   * @return the collection
   */
  public Collection getVerificationTypeInfos(){
    ArrayList result = new ArrayList();
    if (verificationTypeInfoLocals != null){
      for (int i = 0; i < verificationTypeInfoLocals.length; i++) {
        VerificationTypeInfo verificationTypeInfo = verificationTypeInfoLocals[i];
        result.add(verificationTypeInfo);
      }
    }
    if (verificationTypeInfoStack != null){
      for (int i = 0; i < verificationTypeInfoStack.length; i++) {
        VerificationTypeInfo verificationTypeInfo = verificationTypeInfoStack[i];
        result.add(verificationTypeInfo);
      }
    }
    return result;
  }

  private void read(DataInput din) throws java.io.IOException
    {
      verificationTypeInfoLocals = null;
      verificationTypeInfoStack = null;
      u1_frameType = din.readUnsignedByte();
      if (u1_frameType < 64){
        // SAME
      } else if (u1_frameType >= 64 && u1_frameType <= 127){
        // SAME_LOCALS_1_STACK_ITEM;
        verificationTypeInfoStack = new VerificationTypeInfo[1];
        verificationTypeInfoStack[0] = VerificationTypeInfo.create(din);
      } else if (u1_frameType == 247){
        // SAME_LOCALS_1_STACK_ITEM_EXTENDED
        u2_offset_delta = din.readUnsignedShort();
        verificationTypeInfoStack = new VerificationTypeInfo[1];
        verificationTypeInfoStack[0] = VerificationTypeInfo.create(din);
      } else if (u1_frameType >= 248 && u1_frameType <= 250){
        // CHOP
        u2_offset_delta = din.readUnsignedShort();
      } else if (u1_frameType == 251){
        // SAME_FRAME_EXTENDED
        u2_offset_delta = din.readUnsignedShort();
      } else if (u1_frameType >= 252 && u1_frameType <= 254){
        // APPEND
        u2_offset_delta = din.readUnsignedShort();
        int count = u1_frameType - 251;
        verificationTypeInfoStack = new VerificationTypeInfo[count];
        for (int i = 0; i < verificationTypeInfoStack.length; i++) {
          verificationTypeInfoStack[i] = VerificationTypeInfo.create(din);
        }
      } else if (u1_frameType == 255){
        // FULL_FRAME
        u2_offset_delta = din.readUnsignedShort();
        verificationTypeInfoLocals = new VerificationTypeInfo[din.readUnsignedShort()];
        for (int i = 0; i < verificationTypeInfoLocals.length; i++) {
          verificationTypeInfoLocals[i] = VerificationTypeInfo.create(din);
        }
        verificationTypeInfoStack = new VerificationTypeInfo[din.readUnsignedShort()];
        for (int i = 0; i < verificationTypeInfoStack.length; i++) {
          verificationTypeInfoStack[i] = VerificationTypeInfo.create(din);
        }
      } else {
        throw new IllegalArgumentException("Unknown frame type " + u1_frameType);
      }
    }

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
  public void write(DataOutput dout) throws java.io.IOException
    {
      dout.writeByte(u1_frameType);
      if (u1_frameType < 64){
        // SAME
      } else if (u1_frameType >= 64 && u1_frameType <= 127){
        // SAME_LOCALS_1_STACK_ITEM;
        verificationTypeInfoStack[0].write(dout);
      } else if (u1_frameType == 247){
        // SAME_LOCALS_1_STACK_ITEM_EXTENDED
        dout.writeShort(u2_offset_delta);
        verificationTypeInfoStack[0].write(dout);
      } else if (u1_frameType >= 248 && u1_frameType <= 250){
        // CHOP
        dout.writeShort(u2_offset_delta);
      } else if (u1_frameType == 251){
        // SAME_FRAME_EXTENDED
        dout.writeShort(u2_offset_delta);
      } else if (u1_frameType >= 252 && u1_frameType <= 254){
        // APPEND
        dout.writeShort(u2_offset_delta);
        for (int i = 0; i < verificationTypeInfoStack.length; i++) {
          verificationTypeInfoStack[i].write(dout);
        }
      } else if (u1_frameType == 255){
        // FULL_FRAME
        dout.writeShort(u2_offset_delta);
        dout.writeShort(verificationTypeInfoLocals.length);
        for (int i = 0; i < verificationTypeInfoLocals.length; i++) {
          verificationTypeInfoLocals[i].write(dout);
        }
        dout.writeShort(verificationTypeInfoStack.length);
        for (int i = 0; i < verificationTypeInfoStack.length; i++) {
          verificationTypeInfoStack[i].write(dout);
        }
      } else {
        throw new IllegalArgumentException("Unknown frame type " + u1_frameType);
      }
    }
}
