package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'package' entry in the ConstantPool.
 *
 * @author Thomas Behr
 */
public class PackageCpInfo extends AbstractTypeCpInfo {
    /**
     * Instantiates a new Package cp info.
     */
    protected PackageCpInfo() {
    super(CONSTANT_Package);
  }
}
