package com.yworks.yguard.obf.classfile;

import com.yworks.yguard.ParseException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The type Element value info.
 */
public class ElementValueInfo
{
  /**
   * The U 1 tag.
   */
  protected int u1Tag;
  /**
   * The U 2 cp index.
   */
  protected int u2cpIndex;

  /**
   * The U 2 type name index.
   */
  protected int u2typeNameIndex;
  /**
   * The U 2 const name index.
   */
  protected int u2constNameIndex;
  /**
   * The Nested annotation.
   */
  protected AnnotationInfo nestedAnnotation;
  /**
   * The Array values.
   */
  protected ElementValueInfo[] arrayValues;
  
  private ElementValueInfo()
  {}

  /**
   * Create element value info.
   *
   * @param din the din
   * @return the element value info
   * @throws IOException the io exception
   */
  public static ElementValueInfo create(DataInput din) throws IOException
  {
    ElementValueInfo evp = new ElementValueInfo();
    evp.read(din);
    return evp;
  }

  /**
   * Get bool value boolean.
   *
   * @param cp the cp
   * @return the boolean
   */
  public boolean getBoolValue(ConstantPool cp){
    if (u1Tag == 'Z'){
      CpInfo cpEntry = cp.getCpEntry(this.u2cpIndex);
      return cpEntry instanceof IntegerCpInfo && ((IntegerCpInfo) cpEntry).asBool();
    }
    throw new RuntimeException("cannot get bool value of "+u1Tag);
  }

  /**
   * Read.
   *
   * @param din the din
   * @throws IOException the io exception
   */
  protected void read(DataInput din) throws java.io.IOException
  {
    u1Tag = din.readUnsignedByte();
    switch (u1Tag)
    {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'Z':
      case 's':
        u2cpIndex = din.readUnsignedShort();
        break;
      case 'e':
        u2typeNameIndex = din.readUnsignedShort();
        u2constNameIndex = din.readUnsignedShort();
        break;
      case 'c':
        u2cpIndex = din.readUnsignedShort();
        break;
      case '@':
        nestedAnnotation = AnnotationInfo.create(din);
        break;
      case '[':
        int count = din.readUnsignedShort();
        arrayValues = new ElementValueInfo[count];
        for (int i = 0; i < count; i++)
        {
          arrayValues[i] = ElementValueInfo.create(din);
        }
        break;
      default:
        throw new ParseException("Unkown tag type in ElementValuePair: " + u1Tag);
    }
  }

  /**
   * Mark utf 8 refs in info.
   *
   * @param pool the pool
   */
  protected void markUtf8RefsInInfo(ConstantPool pool) {
    switch (u1Tag)
    {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'Z':
      case 's':
        pool.getCpEntry(u2cpIndex).incRefCount();
        break;
      case 'e':
        pool.getCpEntry(u2typeNameIndex).incRefCount();
        pool.getCpEntry(u2constNameIndex).incRefCount();
        break;
      case 'c':
        pool.getCpEntry(u2cpIndex).incRefCount();
        break;
      case '@':
        nestedAnnotation.markUtf8RefsInInfo(pool);
        break;
      case '[':
        for (int i = 0; i < arrayValues.length; i++)
        {
          arrayValues[i].markUtf8RefsInInfo(pool);
        }
        break;
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
    dout.writeByte(u1Tag);
    switch (u1Tag)
    {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'Z':
      case 's':
        dout.writeShort(u2cpIndex);
        break;
      case 'e':
        dout.writeShort(u2typeNameIndex);
        dout.writeShort(u2constNameIndex);
        break;
      case 'c':
        dout.writeShort(u2cpIndex);
        break;
      case '@':
        nestedAnnotation.write(dout);
        break;
      case '[':
        int count = arrayValues.length;
        dout.writeShort(count);
        for (int i = 0; i < count; i++)
        {
          arrayValues[i].write(dout);
        }
        break;
    }
  }
}
