package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a module provides struct in the module attribute section.
 *
 * @author Thomas Behr
 */
public class ModuleProvides {
    /**
     * Reference to {@link ClassCpInfo}
     */
    final int u2providesIndex;
    /**
     * References to {@link ClassCpInfo}
     */
    final int[] u2providesWithIndex;

  private ModuleProvides( final int index, final int[] withIndex ) {
    this.u2providesIndex = index;
    this.u2providesWithIndex = withIndex;
  }

    /**
     * Read module provides.
     *
     * @param din the din
     * @return the module provides
     * @throws IOException the io exception
     */
    static ModuleProvides read( final DataInput din ) throws IOException {
    final int index = din.readUnsignedShort();
    final int withCount = din.readUnsignedShort();
    final int[] withIndex = new int[withCount];
    for (int j = 0; j < withCount; ++j) {
      withIndex[j] = din.readUnsignedShort();
    }

    return new ModuleProvides(index, withIndex);
  }

    /**
     * Write.
     *
     * @param dout the dout
     * @throws IOException the io exception
     */
    void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2providesIndex);
    final int u2providesWithCount = u2providesWithIndex.length;
    dout.writeShort(u2providesWithCount);
    for (int i = 0; i < u2providesWithCount; ++i) {
      dout.writeShort(u2providesWithIndex[i]);
    }
  }
}
