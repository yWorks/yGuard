/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

/**
 * Interface to a class, method, field remapping table.
 *
 * @author Mark Welsh
 */
public interface NameMapper
{
    // Interface Methods -----------------------------------------------------

  /**
   * Return a list of attributes marked to keep.
   *
   * @param className the class name
   * @return the string [ ]
   */
  public String[] getAttrsToKeep(String className) ;

  /**
   * Mapping for fully qualified class name.
   *
   * @param className the class name
   * @return the string
   */
  public String mapClass(String className) ;

  /**
   * Mapping for method name, of fully qualified class.
   *
   * @param className  the class name
   * @param methodName the method name
   * @param descriptor the descriptor
   * @return the string
   */
  public String mapMethod(String className, String methodName, String descriptor) ;

  /**
   * Mapping for an annotation field/method, of fully qualified annotation class.
   *
   * @param className           the class name
   * @param annotationFieldName the annotation field name
   * @return the string
   */
  public String mapAnnotationField(String className, String annotationFieldName) ;

  /**
   * Mapping for field name, of fully qualified class.
   *
   * @param className the class name
   * @param fieldName the field name
   * @return the string
   */
  public String mapField(String className, String fieldName) ;

  /**
   * Mapping for descriptor of field or method.
   *
   * @param descriptor the descriptor
   * @return the string
   */
  public String mapDescriptor(String descriptor) ;

  /**
   * Mapping for signature of field or method.
   *
   * @param signature the signature
   * @return the string
   */
  public String mapSignature(String signature) ;

  /**
   * Mapping for the source file attribute of a file.
   *
   * @param className  the class name
   * @param sourceFile the source file
   * @return the string
   */
  public String mapSourceFile(String className, String sourceFile);

  /**
   * Mapping for the line number table.
   *
   * @param className       the class name
   * @param methodName      the method name
   * @param methodSignature the method signature
   * @param info            the info
   * @return <code>false</code> if the line number table may be discarded
   */
  public boolean mapLineNumberTable(String className, String methodName, String methodSignature, LineNumberTableAttrInfo info);

  /**
   * Map local variable string.
   *
   * @param thisClassName the this class name
   * @param methodName    the method name
   * @param descriptor    the descriptor
   * @param string        the string
   * @return the string
   */
  public String mapLocalVariable(String thisClassName, String methodName, String descriptor, String string);

  /**
   * Mapping for package name.
   *
   * @param packageName the package name
   * @return the string
   */
  public String mapPackage(String packageName) ;
}
