/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 * <p>
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 */
package com.yworks.yguard.obf;

import com.yworks.yguard.obf.classfile.ClassFile;

import java.lang.reflect.Modifier;

/**
 * Base to method and field tree items.
 *
 * @author Mark Welsh
 */
abstract public class MdFd extends TreeItem {
  // Constants -------------------------------------------------------------


  // Fields ----------------------------------------------------------------
  private String descriptor = null;
  private final ObfuscationConfig obfuscationConfig;


  // Class Methods ---------------------------------------------------------


  // Instance Methods ------------------------------------------------------

  /**
   * Ctor.
   *
   * @param parent            the parent
   * @param isSynthetic       the is synthetic
   * @param name              the name
   * @param descriptor        the descriptor
   * @param access            the access
   * @param obfuscationConfig the obfuscation config
   */
  public MdFd( TreeItem parent, boolean isSynthetic, String name, String descriptor, int access, ObfuscationConfig obfuscationConfig ) {
    super(parent, name);
    this.descriptor = descriptor;
    this.obfuscationConfig = obfuscationConfig;
    this.access = access;

    this.isSynthetic = isSynthetic;
    if (name.equals("") || descriptor.equals("") || !(parent instanceof Cl)) {
      System.err.println("Internal error: method/field must have name and descriptor, and have Class or Interface as parent");
    }

    // Disallow obfuscation of 'Synthetic' and native methods and fields
    if (isSynthetic || Modifier.isNative(access)) {
      setOutName(getInName());
    }
  }

  /**
   * Gets obfuscation config.
   *
   * @return the obfuscation config
   */
  public ObfuscationConfig getObfuscationConfig() {
    return obfuscationConfig;
  }

  /**
   * Return the method or field descriptor String.
   *
   * @return the descriptor
   */
  public String getDescriptor() {
    return descriptor;
  }

  /**
   * Return the display name for field.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    int modifiers = getModifiers();
    if (Modifier.isAbstract(modifiers)) {
      sb.append("abstract ");
    }
    if (Modifier.isSynchronized(modifiers)) {
      sb.append("synchronized ");
    }
    if (Modifier.isTransient(modifiers)) {
      sb.append("transient ");
    }
    if (Modifier.isVolatile(modifiers)) {
      sb.append("volatile ");
    }
    if (Modifier.isNative(modifiers)) {
      sb.append("native ");
    }
    if (Modifier.isPublic(modifiers)) {
      sb.append("public ");
    }
    if (Modifier.isProtected(modifiers)) {
      sb.append("protected ");
    }
    if (Modifier.isPrivate(modifiers)) {
      sb.append("private ");
    }
    if (Modifier.isStatic(modifiers)) {
      sb.append("static ");
    }
    if (Modifier.isFinal(modifiers)) {
      sb.append("final ");
    }
    sb.append(getReturnTypeName());
    sb.append(getInName());
    sb.append(getDescriptorName());
    return sb.toString();
  }

  /**
   * Return the display name of the return type.
   *
   * @return the return type name
   */
  protected String getReturnTypeName() {
    String[] types = parseTypes();
    return (types.length > 0 ? types[types.length - 1] : "") + " ";
  }

  /**
   * Return the display name of the descriptor types.
   *
   * @return the descriptor name
   */
  abstract protected String getDescriptorName();

  /**
   * Return the parsed descriptor types array.
   */
  private String[] parsedTypes = null;

  /**
   * Parse types string [ ].
   *
   * @return the string [ ]
   */
  protected String[] parseTypes() {
    if (parsedTypes == null) {
      try {
        parsedTypes = ClassFile.parseDescriptor(getDescriptor(), true);
      } catch (Exception e) {
        parsedTypes = null;
      }
    }
    return parsedTypes;
  }
}
