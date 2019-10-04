package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a bootstrap methods in the bootstrap method attribute section.
 *
 * @author      Sebastian Rheinnecker, yworks
*/
public class BootstrapMethod {
  int u2bootstrapMethodRef;
  int[] u2bootstrapArguments;

  private BootstrapMethod( int methodRef, int[] arguments ) {
    this.u2bootstrapMethodRef = methodRef;
    this.u2bootstrapArguments = arguments;
  }

  static BootstrapMethod read( final DataInput din ) throws IOException {
    int methodRef = din.readUnsignedShort();
    int numArguments = din.readUnsignedShort();
    int[] arguments = new int[numArguments];
    for (int i = 0; i < numArguments; ++i) {
      arguments[i] = din.readUnsignedShort();
    }
    return new BootstrapMethod(methodRef, arguments);
  }

  void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2bootstrapMethodRef);
    final int u2numBootstrapArguments = u2bootstrapArguments.length;
    dout.writeShort(u2numBootstrapArguments);
    for (int j = 0; j < u2numBootstrapArguments; ++j) {
      dout.writeShort(u2bootstrapArguments[j]);
    }
  }

  public int getBootstrapMethodRef() {
    return u2bootstrapMethodRef;
  }

  public int[] getBootstrapArguments() {
    return u2bootstrapArguments;
  }
}
