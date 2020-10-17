package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a method parameters attribute.
 *
 * @author Thomas Behr
 */
public class MethodParametersAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private MethodParameter[] parameters;


  // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Method parameters attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
// Instance Methods ------------------------------------------------------
  MethodParametersAttrInfo( final ClassFile cf, final int attrNameIndex, final int attrLength ) {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Returns <code>"MethodParameters"</code>.
   */
  protected String getAttrName() {
    return ATTR_MethodParameters;
  }

  /**
   * Check for references in the 'info' data to the constant pool and mark them.
   */
  protected void markUtf8RefsInInfo( final ConstantPool pool ) {
    for (int i = 0; i < parameters.length; ++i) {
      final int index = parameters[i].u2nameIndex;
      if (index > 0) {
        pool.incRefCount(index);
      }
    }
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    final int u1parameterCount = din.readUnsignedByte();
    parameters = new MethodParameter[u1parameterCount];
    for (int i = 0; i < u1parameterCount; ++i) {
      parameters[i] = MethodParameter.read(din);
    }
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    final int u1parameterCount = parameters.length;
    dout.writeByte(u1parameterCount);
    for (int i = 0; i < u1parameterCount; ++i) {
      parameters[i].write(dout);
    }
  }
}
