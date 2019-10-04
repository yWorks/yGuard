/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author may be contacted at yguard@yworks.com 
 *
 * Java and all Java-based marks are trademarks or registered 
 * trademarks of Sun Microsystems, Inc. in the U.S. and other countries.
 */
package com.yworks.yguard.obf.classfile;

import java.io.*;

import com.yworks.yguard.obf.*;


/**
 * Representation of a field or method from a class-file.
 *
 * @author      Mark Welsh
 */
abstract public class ClassItemInfo implements ClassConstants
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------
    private int u2accessFlags;
    private int u2nameIndex;
    private int u2descriptorIndex;
    protected int u2attributesCount;
    protected AttrInfo attributes[];

    private ClassFile cf;
    private boolean isSynthetic = false;
  // marker instead of null
  private static final ObfuscationConfig DUMMY = new ObfuscationConfig(true, true);
  private ObfuscationConfig obfuscationConfig = DUMMY;


  // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    protected ClassItemInfo(ClassFile cf) {this.cf = cf;}

  public static ObfuscationConfig getObfuscationConfig(AttrInfo[] attributes) {
    if (attributes == null) return null;
    for (int i = 0; i < attributes.length; i++) {
      AttrInfo attribute = attributes[i];

      if (attribute instanceof RuntimeVisibleAnnotationsAttrInfo){
        RuntimeVisibleAnnotationsAttrInfo annotation = (RuntimeVisibleAnnotationsAttrInfo) attribute;
        ClassFile owner = annotation.getOwner();
        AnnotationInfo[] clAnnotations = annotation.getAnnotations();
        for (int j = 0; j < clAnnotations.length; j++) {
          Utf8CpInfo cpEntry = (Utf8CpInfo) owner.getCpEntry(annotation.getU2TypeIndex(j));
          String currentAnnotationName = cpEntry.getString();

          if (currentAnnotationName != null && currentAnnotationName.contains(ObfuscationConfig.annotationClassName)){
            // found class with matching annotation
            AnnotationInfo clAnnotation = clAnnotations[j];
            boolean exclude = getExclude(clAnnotation, owner);
            boolean applyToMembers = getApplyToMembers(clAnnotation, owner);
            return new ObfuscationConfig(exclude, applyToMembers);
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns true if the given annotation indicates to retain the entity
   */
  private static boolean getExclude(AnnotationInfo clAnnotation, ClassFile owner) {
    ElementValuePairInfo[] elementValuePairs = clAnnotation.getElementValuePairs();

    for (int i = 0; i < elementValuePairs.length; i++) {
      ElementValuePairInfo elementValuePair = elementValuePairs[i];
      Utf8CpInfo cpEntry = (Utf8CpInfo) owner.getCpEntry(elementValuePair.getU2ElementNameIndex());
      if ("exclude".equals(cpEntry.getString())){
        return elementValuePair.getElementValue().getBoolValue(owner.getConstantPool());
      }
    }
    // Default: yes, exclude from obfuscation
    return true;
  }

  /**
   * Returns true if the given annotation indicates to apply the retain indicator to all members of the class
   */
  private static boolean getApplyToMembers(AnnotationInfo clAnnotation, ClassFile owner) {
    ElementValuePairInfo[] elementValuePairs = clAnnotation.getElementValuePairs();

    for (int i = 0; i < elementValuePairs.length; i++) {
      ElementValuePairInfo elementValuePair = elementValuePairs[i];
      Utf8CpInfo cpEntry = (Utf8CpInfo) owner.getCpEntry(elementValuePair.getU2ElementNameIndex());
      if ("applyToMembers".equals(cpEntry.getString())){
        return elementValuePair.getElementValue().getBoolValue(owner.getConstantPool());
      }
    }
    // Default: yes, apply to members
    return true;
  }

  /** Is the field or method 'Synthetic'? */
    public boolean isSynthetic() {return isSynthetic;}

    /** Return method/field name index into Constant Pool. */
    protected int getNameIndex() {return u2nameIndex;}

    /** Set the method/field name index. */
    protected void setNameIndex(int index) {u2nameIndex = index;}

    /** Return method/field descriptor index into Constant Pool. */
    protected int getDescriptorIndex() {return u2descriptorIndex;}

    /** Set the method/field descriptor index. */
    protected void setDescriptorIndex(int index) {u2descriptorIndex = index;}

    /** Return method/field string name. */
    public String getName()
    {
        return ((Utf8CpInfo)cf.getCpEntry(u2nameIndex)).getString();
    }

    /** Return descriptor string. */
    public String getDescriptor()
    {
        return ((Utf8CpInfo)cf.getCpEntry(u2descriptorIndex)).getString();
    }

    /** Return access flags. */
    public int getAccessFlags()
    {
        return u2accessFlags;
    }

    /**
     * Trim attributes from the classfile ('Code', 'Exceptions', 'ConstantValue'
     * are preserved, all others except the list in the String[] are killed).
     */
    protected void trimAttrsExcept(String[] keepAttrs) 
    {
        // Traverse all attributes, removing all except those on 'keep' list
        for (int i = 0; i < attributes.length; i++)
        {
            if (Tools.isInArray(attributes[i].getAttrName(), keepAttrs))
            {
                attributes[i].trimAttrsExcept(keepAttrs);
            }
            else
            {
                attributes[i] = null;
            }
        }

        // Delete the marked attributes
        AttrInfo[] left = new AttrInfo[attributes.length];
        int j = 0;
        for (int i = 0; i < attributes.length; i++)
        {
            if (attributes[i] != null)
            {
                left[j++] = attributes[i];
            }
        }
        attributes = new AttrInfo[j];
        System.arraycopy(left, 0, attributes, 0, j);
        u2attributesCount = j;
    }

    /** Check for Utf8 references to constant pool and mark them. */
    protected void markUtf8Refs(ConstantPool pool) 
    {
        pool.incRefCount(u2nameIndex);
        pool.incRefCount(u2descriptorIndex);
        for (int i = 0; i < attributes.length; i++)
        {
            attributes[i].markUtf8Refs(pool);
        }
    }

    /** Import the field or method data to internal representation. */
    protected void read(DataInput din) throws java.io.IOException
    {
        u2accessFlags = din.readUnsignedShort();
        u2nameIndex = din.readUnsignedShort();
        u2descriptorIndex = din.readUnsignedShort();
        u2attributesCount = din.readUnsignedShort();
        attributes = new AttrInfo[u2attributesCount];
        for (int i = 0; i < u2attributesCount; i++)
        {
            attributes[i] = AttrInfo.create(din, cf);
            if (attributes[i].getAttrName().equals(ATTR_Synthetic))
            {
                isSynthetic = true;
            }
        }
    }

    /** Export the representation to a DataOutput stream. */
    public void write(DataOutput dout) throws java.io.IOException
    {
        if (dout == null) throw new NullPointerException("No output stream was provided.");
        dout.writeShort(u2accessFlags);
        dout.writeShort(u2nameIndex);
        dout.writeShort(u2descriptorIndex);
        dout.writeShort(u2attributesCount);
        for (int i = 0; i < u2attributesCount; i++)
        {
            attributes[i].write(dout);
        }
    }

  public ObfuscationConfig getObfuscationConfig() {
    if (obfuscationConfig == DUMMY){
      obfuscationConfig = getObfuscationConfig(attributes);
    }
    return obfuscationConfig;
  }
}
