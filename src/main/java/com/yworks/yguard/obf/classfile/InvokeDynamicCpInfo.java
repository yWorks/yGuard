package com.yworks.yguard.obf.classfile;

/**
 * Representation of a 'invokedynamic' entry in the ConstantPool.
 *
 * @author Sebastian Rheinnecker, yworks
 */
public class InvokeDynamicCpInfo extends AbstractDynamicCpInfo {
    /**
     * Instantiates a new Invoke dynamic cp info.
     */
    protected InvokeDynamicCpInfo() {
    super(CONSTANT_InvokeDynamic);
  }
}
