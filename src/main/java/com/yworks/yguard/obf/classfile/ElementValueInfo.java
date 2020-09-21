/*
 * ElementValueInfo.java
 *
 * Created on April 20, 2005, 4:19 PM
 */

package com.yworks.yguard.obf.classfile;

import com.yworks.yguard.ParseException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ElementValueInfo
{
  protected int u1Tag;
  protected int u2cpIndex;
  
  protected int u2typeNameIndex;
  protected int u2constNameIndex;
  protected AnnotationInfo nestedAnnotation;
  protected ElementValueInfo[] arrayValues;
  
  private ElementValueInfo()
  {}
  
  public static ElementValueInfo create(DataInput din) throws IOException
  {
    ElementValueInfo evp = new ElementValueInfo();
    evp.read(din);
    return evp;
  }

  public boolean getBoolValue(ConstantPool cp){
    if (u1Tag == 'Z'){
      CpInfo cpEntry = cp.getCpEntry(this.u2cpIndex);
      return cpEntry instanceof IntegerCpInfo && ((IntegerCpInfo) cpEntry).asBool();
    }
    throw new RuntimeException("cannot get bool value of "+u1Tag);
  }
  
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
  
  /** Export the representation to a DataOutput stream. */
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
