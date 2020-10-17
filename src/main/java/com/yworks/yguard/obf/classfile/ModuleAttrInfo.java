package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a module attribute.
 *
 * @author Thomas Behr
 */
public class ModuleAttrInfo extends AttrInfo {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2moduleNameIndex;
  private int u2moduleFlags;
  private int u2moduleVersionIndex;

  private ModuleRequires[] requires;

  private ModuleExports[] exports;

  private ModuleOpens[] opens;

  private int[] u2usesIndex;

  private ModuleProvides[] provides;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Module attr info.
   *
   * @param cf            the cf
   * @param attrNameIndex the attr name index
   * @param attrLength    the attr length
   */
// Instance Methods ------------------------------------------------------
  ModuleAttrInfo( final ClassFile cf, final int attrNameIndex, final int attrLength ) {
    super(cf, attrNameIndex, attrLength);
  }

  /**
   * Gets module name index.
   *
   * @return the module name index
   */
  int getModuleNameIndex() {
    return u2moduleNameIndex;
  }

  /**
   * Sets module name index.
   *
   * @param idx the idx
   */
  void setModuleNameIndex( final int idx ) {
    this.u2moduleNameIndex = idx;
  }

  /**
   * Returns <code>"Module"</code>.
   */
  protected String getAttrName() {
    return ATTR_Module;
  }

  /**
   * Check for references in the 'info' data to the constant pool and mark them.
   */
  protected void markUtf8RefsInInfo( final ConstantPool pool ) {
    if (u2moduleVersionIndex > 0) {
      pool.incRefCount(u2moduleVersionIndex);
    }

    for (int i = 0; i < requires.length; ++i) {
      pool.incRefCount(requires[i].u2requiresVersionIndex);
    }
  }

  /**
   * Read the data following the header.
   */
  protected void readInfo( final DataInput din ) throws IOException {
    u2moduleNameIndex = din.readUnsignedShort();
    u2moduleFlags = din.readUnsignedShort();
    u2moduleVersionIndex = din.readUnsignedShort();

    final int u2requiresCount = din.readUnsignedShort();
    requires = new ModuleRequires[u2requiresCount];
    for (int i = 0; i < u2requiresCount; ++i) {
      requires[i] = ModuleRequires.read(din);
    }

    final int u2exportsCount = din.readUnsignedShort();
    exports = new ModuleExports[u2exportsCount];
    for (int i = 0; i < u2exportsCount; ++i) {
      exports[i] = ModuleExports.read(din);
    }

    final int u2opensCount = din.readUnsignedShort();
    opens = new ModuleOpens[u2opensCount];
    for (int i = 0; i < u2opensCount; ++i) {
      opens[i] = ModuleOpens.read(din);
    }

    final int u2usesCount = din.readUnsignedShort();
    u2usesIndex = new int[u2usesCount];
    for (int i = 0; i < u2usesCount; ++i) {
      u2usesIndex[i] = din.readUnsignedShort();
    }

    final int u2providesCount = din.readUnsignedShort();
    provides = new ModuleProvides[u2providesCount];
    for (int i = 0; i < u2providesCount; ++i) {
      provides[i] = ModuleProvides.read(din);
    }
  }

  /**
   * Export data following the header to a DataOutput stream.
   */
  public void writeInfo( final DataOutput dout ) throws IOException {
    dout.writeShort(u2moduleNameIndex);
    dout.writeShort(u2moduleFlags);
    dout.writeShort(u2moduleVersionIndex);

    final int u2requiresCount = requires.length;
    dout.writeShort(u2requiresCount);
    for (int i = 0; i < u2requiresCount; ++i) {
      requires[i].write(dout);
    }

    final int u2exportsCount = exports.length;
    dout.writeShort(u2exportsCount);
    for (int i = 0; i < u2exportsCount; ++i) {
      exports[i].write(dout);
    }

    final int u2opensCount = opens.length;
    dout.writeShort(u2opensCount);
    for (int i = 0; i < u2opensCount; ++i) {
      opens[i].write(dout);
    }

    final int u2usesCount = u2usesIndex.length;
    dout.writeShort(u2usesCount);
    for (int i = 0; i < u2usesCount; ++i) {
      dout.writeShort(u2usesIndex[i]);
    }

    final int u2providesCount = provides.length;
    dout.writeShort(u2providesCount);
    for (int i = 0; i < u2providesCount; ++i) {
      provides[i].write(dout);
    }
  }
}
