package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'dynamic' entry in the ConstantPool.
 *
 * @author Thomas Behr
 */
public class DynamicCpInfo extends AbstractDynamicCpInfo {
  /**
   * Instantiates a new Dynamic cp info.
   */
  protected DynamicCpInfo() {
    super(CONSTANT_Dynamic);
  }
}
