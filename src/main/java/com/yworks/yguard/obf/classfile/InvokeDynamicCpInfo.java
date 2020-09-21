package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a 'invokedynamic' entry in the ConstantPool.
 *
 * @author      Sebastian Rheinnecker, yworks
 */
public class InvokeDynamicCpInfo extends AbstractDynamicCpInfo {
  protected InvokeDynamicCpInfo() {
    super(CONSTANT_InvokeDynamic);
  }
}
