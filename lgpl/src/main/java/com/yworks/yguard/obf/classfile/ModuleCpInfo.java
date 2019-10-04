package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'module' entry in the ConstantPool.
 * @author Thomas Behr
 */
public class ModuleCpInfo extends AbstractTypeCpInfo {
  protected ModuleCpInfo() {
    super(CONSTANT_Module);
  }
}
