package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a module exports struct in the module attribute section.
 * @author Thomas Behr
 */
public class ModuleExports {
  /** Reference to {@link PackageCpInfo} */
  final int u2exportsIndex;
  /** Access flags value */
  final int u2exportsFlags;
  /** References to {@link ModuleCpInfo} */
  final int[] u2exportsToIndex;

  private ModuleExports( final int index, final int flags, final int[] toIndex ) {
    this.u2exportsIndex = index;
    this.u2exportsFlags = flags;
    this.u2exportsToIndex = toIndex;
  }

  int getExportsIndex() {
    return u2exportsIndex;
  }

  static ModuleExports read( final DataInput din ) throws IOException {
    final int index = din.readUnsignedShort();
    final int flags = din.readUnsignedShort();
    final int toCount = din.readUnsignedShort();
    final int[] toIndex = new int[toCount];
    for (int j = 0; j < toCount; ++j) {
      toIndex[j] = din.readUnsignedShort();
    }

    return new ModuleExports(index, flags, toIndex);
  }

  void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2exportsIndex);
    dout.writeShort(u2exportsFlags);
    final int u2exportsToCount = u2exportsToIndex.length;
    dout.writeShort(u2exportsToCount);
    for (int i = 0; i < u2exportsToCount; ++i) {
      dout.writeShort(u2exportsToIndex[i]);
    }
  }
}
