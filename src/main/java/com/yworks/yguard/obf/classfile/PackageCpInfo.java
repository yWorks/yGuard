package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'package' entry in the ConstantPool.
 * @author Thomas Behr
 */
public class PackageCpInfo extends AbstractTypeCpInfo {
  protected PackageCpInfo() {
    super(CONSTANT_Package);
  }
}
