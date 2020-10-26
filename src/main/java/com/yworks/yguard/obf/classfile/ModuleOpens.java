package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a module opens struct in the module attribute section.
 *
 * @author Thomas Behr
 */
public class ModuleOpens {
    /**
     * Reference to {@link PackageCpInfo}
     */
    final int u2opensIndex;
    /**
     * Access flags value
     */
    final int u2opensFlags;
    /**
     * References to {@link ModuleCpInfo}
     */
    final int[] u2opensToIndex;

  private ModuleOpens( final int index, final int flags, final int[] toIndex ) {
    this.u2opensIndex = index;
    this.u2opensFlags = flags;
    this.u2opensToIndex = toIndex;
  }

    /**
     * Read module opens.
     *
     * 
		 * @param din the din
     * 
		 * @return the module opens
     * @throws IOException the io exception
     */
    static ModuleOpens read( final DataInput din ) throws IOException {
    final int index = din.readUnsignedShort();
    final int flags = din.readUnsignedShort();
    final int toCount = din.readUnsignedShort();
    final int[] toIndex = new int[toCount];
    for (int j = 0; j < toCount; ++j) {
      toIndex[j] = din.readUnsignedShort();
    }

    return new ModuleOpens(index, flags, toIndex);
  }

    /**
     * Write.
     *
     * 
		 * @param dout the dout
     * @throws IOException the io exception
     */
    void write( final DataOutput dout ) throws IOException {
    dout.writeShort(u2opensIndex);
    dout.writeShort(u2opensFlags);
    final int u2opensToCount = u2opensToIndex.length;
    dout.writeShort(u2opensToCount);
    for (int i = 0; i < u2opensToCount; ++i) {
      dout.writeShort(u2opensToIndex[i]);
    }
  }
}
