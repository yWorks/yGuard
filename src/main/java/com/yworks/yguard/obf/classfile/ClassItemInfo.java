/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

import com.yworks.yguard.obf.ObfuscationConfig;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * Representation of a field or method from a class-file.
 *
 * @author Mark Welsh
 */
abstract public class ClassItemInfo implements ClassConstants
{
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private int u2accessFlags;
  private int u2nameIndex;
  private int u2descriptorIndex;
  /**
   * The U 2 attributes count.
   */
  protected int u2attributesCount;
  /**
   * The Attributes.
   */
  protected AttrInfo attributes[];

  private ClassFile cf;
  private boolean isSynthetic = false;
  // marker instead of null
  private static final ObfuscationConfig DUMMY = new ObfuscationConfig(true, true);
  private ObfuscationConfig obfuscationConfig = DUMMY;


  // Class Methods ---------------------------------------------------------


  /**
   * Instantiates a new Class item info.
   *
   * @param cf the cf
   */
  protected ClassItemInfo(ClassFile cf) {this.cf = cf;}

  // Instance Methods ------------------------------------------------------
  /**
   * Gets obfuscation config.
   *
   * @param name       the name
   * @param attributes the attributes
   * @return the obfuscation config
   */
  public static ObfuscationConfig getObfuscationConfig(String name, AttrInfo[] attributes) {
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
            Logger.getInstance().log(String.format("Applied annotation %s to %s", ObfuscationConfig.annotationClassName, name));
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

  /**
   * Is the field or method 'Synthetic'?
   *
   * @return the boolean
   */
  public boolean isSynthetic() {return isSynthetic;}

  /**
   * Return method/field name index into Constant Pool.
   *
   * @return the name index
   */
  protected int getNameIndex() {return u2nameIndex;}

  /**
   * Set the method/field name index.
   *
   * @param index the index
   */
  protected void setNameIndex(int index) {u2nameIndex = index;}

  /**
   * Return method/field descriptor index into Constant Pool.
   *
   * @return the descriptor index
   */
  protected int getDescriptorIndex() {return u2descriptorIndex;}

  /**
   * Set the method/field descriptor index.
   *
   * @param index the index
   */
  protected void setDescriptorIndex(int index) {u2descriptorIndex = index;}

  /**
   * Return method/field string name.
   *
   * @return the name
   */
  public String getName()
  {
    return ((Utf8CpInfo) cf.getCpEntry(u2nameIndex)).getString();
  }

  /**
   * Return descriptor string.
   *
   * @return the descriptor
   */
  public String getDescriptor()
  {
    return ((Utf8CpInfo) cf.getCpEntry(u2descriptorIndex)).getString();
  }

  /**
   * Return access flags.
   *
   * @return the access flags
   */
  public int getAccessFlags()
    {
        return u2accessFlags;
    }

  /**
   * Trim attributes from the classfile ('Code', 'Exceptions', 'ConstantValue'
   * are preserved, all others except the list in the String[] are killed).
   *
   * @param keepAttrs the keep attrs
   */
  protected void trimAttrsExcept(String[] keepAttrs)
  {
    attributes = AttrInfo.filter(attributes, keepAttrs);
    u2attributesCount = attributes.length;
  }

  /**
   * Check for Utf8 references to constant pool and mark them.
   *
   * @param pool the pool
   */
  protected void markUtf8Refs(ConstantPool pool)
  {
    pool.incRefCount(u2nameIndex);
    pool.incRefCount(u2descriptorIndex);
    for (int i = 0; i < attributes.length; i++)
    {
      attributes[i].markUtf8Refs(pool);
    }
  }

  /**
   * Import the field or method data to internal representation.
   *
   * @param din the din
   * @throws IOException the io exception
   */
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

  /**
   * Export the representation to a DataOutput stream.
   *
   * @param dout the dout
   * @throws IOException the io exception
   */
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

  /**
   * Gets obfuscation config.
   *
   * @return the obfuscation config
   */
  public ObfuscationConfig getObfuscationConfig() {
    if (obfuscationConfig == DUMMY){
      obfuscationConfig = getObfuscationConfig(String.format("%s#%s", this.cf.getName(), this.getName()), attributes);
    }
    return obfuscationConfig;
  }
}
