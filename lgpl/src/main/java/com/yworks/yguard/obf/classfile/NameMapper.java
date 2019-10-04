/*
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
