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
 * @author      Mark Welsh
 */
public interface NameMapper
{
    // Interface Methods -----------------------------------------------------
    /** Return a list of attributes marked to keep. */
    public String[] getAttrsToKeep(String className) ;

    /** Mapping for fully qualified class name. */
    public String mapClass(String className) ;

    /** Mapping for method name, of fully qualified class. */
    public String mapMethod(String className, String methodName, String descriptor) ;

    /** Mapping for an annotation field/method, of fully qualified annotation class. */
    public String mapAnnotationField(String className, String annotationFieldName) ;

    /** Mapping for field name, of fully qualified class. */
    public String mapField(String className, String fieldName) ;

    /** Mapping for descriptor of field or method. */
    public String mapDescriptor(String descriptor) ;

    /** Mapping for signature of field or method. */
    public String mapSignature(String signature) ;
    
    /** Mapping for the source file attribute of a file. */
    public String mapSourceFile(String className, String sourceFile);

    /**
     * Mapping for the line number table.
     * @return <code>false</code> if the line number table may be discarded
     */
    public boolean mapLineNumberTable(String className, String methodName, String methodSignature, LineNumberTableAttrInfo info);
  
    public String mapLocalVariable(String thisClassName, String methodName, String descriptor, String string);

    /** Mapping for package name. */
    public String mapPackage(String packageName) ;
}
