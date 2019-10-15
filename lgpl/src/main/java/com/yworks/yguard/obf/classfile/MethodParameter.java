package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a method parameter struct in the method parameters
 * attribute section.
 * @author Thomas Behr
 */
public class MethodParameter {
  /**
   * Reference to {@link Utf8CpInfo} or {@code 0}.
   */
  final int u2nameIndex;
  /**
   * Access flags value
   */
  final int u2accessFlags;

  private MethodParameter( final int index, final int flags ) {
    this.u2nameIndex = index;
    this.u2accessFlags = flags;
  }

  static MethodParameter read( final DataInput din ) throws IOException {
    final int index = din.readUnsignedShort();
    final int flags = din.readUnsignedShort();
    return new MethodParameter(index, flags);
  }

  void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2nameIndex);
    dout.writeShort(u2accessFlags);
  }
}