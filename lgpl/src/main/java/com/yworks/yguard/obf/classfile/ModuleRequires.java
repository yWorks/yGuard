package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a module requires struct in the module attribute section.
 * @author Thomas Behr
 */
public class ModuleRequires {
  /** Reference to {@link ModuleCpInfo} */
  final int u2requiresIndex;
  /** Access flags value */
  final int u2requiresFlags;
  /** Reference to {@link Utf8CpInfo} */
  final int u2requiresVersionIndex;

  private ModuleRequires( final int index, final int flags, final int versionIndex ) {
    this.u2requiresIndex = index;
    this.u2requiresFlags = flags;
    this.u2requiresVersionIndex = versionIndex;
  }

  static ModuleRequires read( final DataInput din ) throws IOException {
    final int index = din.readUnsignedShort();
    final int flags = din.readUnsignedShort();
    final int versionIndex = din.readUnsignedShort();
    return new ModuleRequires(index, flags, versionIndex);
  }

  void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2requiresIndex);
    dout.writeShort(u2requiresFlags);
    dout.writeShort(u2requiresVersionIndex);
  }
}
