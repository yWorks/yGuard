package com.yworks.yguard.obf.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Representation of a bootstrap-methods attribute.
 *
 * @author Sebastian Rheinnecker, yworks
 */
public class BootstrapMethodsAttrInfo extends AttrInfo {
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private BootstrapMethod[] bootstrapMethods;

    // Class Methods ---------------------------------------------------------


    /**
     * Instantiates a new Bootstrap methods attr info.
     *
     * @param cf            the cf
     * @param attrNameIndex the attr name index
     * @param attrLength    the attr length
     */
// Instance Methods ------------------------------------------------------
    protected BootstrapMethodsAttrInfo(ClassFile cf, int attrNameIndex, int attrLength)
    {
      super(cf, attrNameIndex, attrLength);
    }

    /** Return the String name of the attribute; over-ride this in sub-classes. */
    protected String getAttrName()
    {
      return ATTR_BootstrapMethods;
    }

    /** Read the data following the header. */
    protected void readInfo(DataInput din) throws IOException {
      final int u2numBootstrapMethods = din.readUnsignedShort();
      bootstrapMethods = new BootstrapMethod[u2numBootstrapMethods];
      for (int i = 0; i < u2numBootstrapMethods; ++i) {
        bootstrapMethods[i] = BootstrapMethod.read(din);
      }
    }

    /** Export data following the header to a DataOutput stream. */
    public void writeInfo(DataOutput dout) throws IOException {
      final int u2numBootstrapMethods = bootstrapMethods.length;
      dout.writeShort(u2numBootstrapMethods);
      for (int i = 0; i < u2numBootstrapMethods; ++i) {
        bootstrapMethods[i].write(dout);
      }
    }

    /**
     * Get bootstrap methods bootstrap method [ ].
     *
     * @return the bootstrap method [ ]
     */
    public BootstrapMethod[] getBootstrapMethods() {
      return bootstrapMethods;
    }
}
