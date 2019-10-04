package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Representation of an VerificationTypeInfo struct 
 */
public class VerificationTypeInfo {
  private int u2_cPoolIndex;
  private int u2_offset;
  private int u1_tag;
  // Constants -------------------------------------------------------------

  // Class Methods ---------------------------------------------------------

  public static VerificationTypeInfo create(DataInput din) throws java.io.IOException {
    if (din == null) throw new NullPointerException("DataInput cannot be null!");
    VerificationTypeInfo vti = new VerificationTypeInfo();
    vti.read(din);
    return vti;
  }


  // Instance Methods ------------------------------------------------------
  private VerificationTypeInfo() {
  }

  /** Check for Utf8 references to constant pool and mark them. */
  protected void markUtf8Refs(ConstantPool pool) {
    switch (u1_tag) {
      case 0: //ITEM_Top;
        break;
      case 1: //ITEM_Integer;
        break;
      case 2: //ITEM_Float;
        break;
      case 3: //ITEM_Double;
        break;
      case 4: //ITEM_Long;
        break;
      case 5: //ITEM_Null;
        break;
      case 6: //ITEM_UninitializedThis;
        break;
      case 7: //ITEM_Object;
        break;
      case 8: //ITEM_Uninitialized;
        break;
      default:
        throw new IllegalArgumentException("Unkown tag " + u1_tag);
    }
  }

  private void read(DataInput din) throws java.io.IOException {
    u2_cPoolIndex = -1;
    u1_tag = din.readUnsignedByte();
    switch (u1_tag) {
      case 0: //ITEM_Top;
        break;
      case 1: //ITEM_Integer;
        break;
      case 2: //ITEM_Float;
        break;
      case 3: //ITEM_Double;
        break;
      case 4: //ITEM_Long;
        break;
      case 5: //ITEM_Null;
        break;
      case 6: //ITEM_UninitializedThis;
        break;
      case 7: //ITEM_Object;
        u2_cPoolIndex = din.readUnsignedShort();
        break;
      case 8: //ITEM_Uninitialized;
        u2_offset = din.readUnsignedShort();
        break;
      default:
        throw new IllegalArgumentException("Unkown tag " + u1_tag);
    }
  }

  /** Export the representation to a DataOutput stream. */
  public void write(DataOutput dout) throws java.io.IOException {
    dout.writeByte(u1_tag);
    switch (u1_tag) {
      case 0: //ITEM_Top;
        break;
      case 1: //ITEM_Integer;
        break;
      case 2: //ITEM_Float;
        break;
      case 3: //ITEM_Double;
        break;
      case 4: //ITEM_Long;
        break;
      case 5: //ITEM_Null;
        break;
      case 6: //ITEM_UninitializedThis;
        break;
      case 7: //ITEM_Object;
        dout.writeShort(u2_cPoolIndex);
        break;
      case 8: //ITEM_Uninitialized;
        dout.writeShort(u2_offset);
        break;
      default:
        throw new IllegalArgumentException("Unkown tag " + u1_tag);
    }
  }
}
