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

public class ElementValuePairInfo
{
  protected int u2ElementNameIndex;
  protected ElementValueInfo elementValue;
  
  private ElementValuePairInfo()
  {}
  
  public static ElementValuePairInfo create(DataInput din) throws IOException
  {
    ElementValuePairInfo evp = new ElementValuePairInfo();
    evp.read(din);
    return evp;
  }
  
  protected void read(DataInput din) throws java.io.IOException
  {
    u2ElementNameIndex = din.readUnsignedShort();
    elementValue = ElementValueInfo.create(din);
  }

  protected void markUtf8RefsInInfo(ConstantPool pool) {
    pool.getCpEntry(u2ElementNameIndex).incRefCount();
    elementValue.markUtf8RefsInInfo(pool);
  }
  
  /** Export the representation to a DataOutput stream.
   * @param dout Export the representation to a DataOutput stream
   * @throws IOException
   */
  public void write(DataOutput dout) throws java.io.IOException
  {
    dout.writeShort(u2ElementNameIndex);
    elementValue.write(dout);
  }

  public int getU2ElementNameIndex() {
    return u2ElementNameIndex;
  }

  public ElementValueInfo getElementValue() {
    return elementValue;
  }
}
