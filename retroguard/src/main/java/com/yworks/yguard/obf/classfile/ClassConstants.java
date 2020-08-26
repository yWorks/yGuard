/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
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
 * The author may be contacted at markw@retrologic.com 
 *
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf.classfile;

import java.io.*;
import java.util.*;

/**
 * Constants used in representing a Java class-file (*.class).
 *
 * @author      Mark Welsh
 */
public interface  ClassConstants
{
    // Constants -------------------------------------------------------------
    public static final int MAGIC = 0xCAFEBABE;

    public static final int MINOR_VERSION_MAX = 3;
    public static final int MAJOR_VERSION     = 0x39;

    public static final int ACC_PUBLIC      = 0x0001;
    public static final int ACC_PRIVATE     = 0x0002;
    public static final int ACC_PROTECTED   = 0x0004;
    public static final int ACC_STATIC      = 0x0008;
    public static final int ACC_FINAL       = 0x0010;
    public static final int ACC_SUPER       = 0x0020;
    public static final int ACC_SYNCHRONIZED= 0x0020;
    public static final int ACC_VOLATILE    = 0x0040;
    public static final int ACC_TRANSIENT   = 0x0080;
    public static final int ACC_NATIVE      = 0x0100;
    public static final int ACC_INTERFACE   = 0x0200;
    public static final int ACC_ABSTRACT    = 0x0400;
    public static final int ACC_SYNTHETIC   = 0x1000;
    public static final int ACC_ANNOTATION  = 0x2000;
    public static final int ACC_ENUM        = 0x4000;
    public static final int ACC_BRIDGE      = 0x0040;
    public static final int ACC_VARARGS     = 0x0080;

    public static final int CONSTANT_Utf8               = 1;
    public static final int CONSTANT_Integer            = 3;
    public static final int CONSTANT_Float              = 4;
    public static final int CONSTANT_Long               = 5;
    public static final int CONSTANT_Double             = 6;
    public static final int CONSTANT_Class              = 7;
    public static final int CONSTANT_String             = 8;
    public static final int CONSTANT_Fieldref           = 9;
    public static final int CONSTANT_Methodref          = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_NameAndType        = 12;
    // new in java 7
    public static final int CONSTANT_MethodHandle       = 15;
    // new in java 7
    public static final int CONSTANT_MethodType         = 16;
    // new in java 11
    public static final int CONSTANT_Dynamic            = 17;
    // new in java 7
    public static final int CONSTANT_InvokeDynamic      = 18;
    // new in java 9
    public static final int CONSTANT_Module             = 19;
    // new in java 9
    public static final int CONSTANT_Package            = 20;

    public static final String ATTR_Unknown             = "Unknown";
    public static final String ATTR_Code                = "Code";
    public static final String ATTR_ConstantValue       = "ConstantValue";
    public static final String ATTR_Exceptions          = "Exceptions";
    public static final String ATTR_StackMapTable       = "StackMapTable";
    public static final String ATTR_LineNumberTable     = "LineNumberTable";
    public static final String ATTR_SourceFile          = "SourceFile";
    public static final String ATTR_SourceDebug         = "SourceDebug";
    public static final String ATTR_LocalVariableTable  = "LocalVariableTable";
    public static final String ATTR_InnerClasses        = "InnerClasses";
    public static final String ATTR_Synthetic           = "Synthetic";
    public static final String ATTR_Deprecated          = "Deprecated";
    public static final String ATTR_LocalVariableTypeTable = "LocalVariableTypeTable";
    public static final String ATTR_Signature           = "Signature";
    public static final String ATTR_EnclosingMethod     = "EnclosingMethod";
    public static final String ATTR_RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";
    public static final String ATTR_RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations";
    public static final String ATTR_RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations";
    public static final String ATTR_RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";
    public static final String ATTR_AnnotationDefault   = "AnnotationDefault";
    public static final String ATTR_Bridge = "Bridge";
    public static final String ATTR_Enum = "Enum";
    public static final String ATTR_Varargs = "Varargs";
    // new in java 7
    public static final String ATTR_BootstrapMethods = "BootstrapMethods";
    // new in java 8
    public static final String ATTR_RuntimeVisibleTypeAnnotations = "RuntimeVisibleTypeAnnotations";
    public static final String ATTR_RuntimeInvisibleTypeAnnotations = "RuntimeInvisibleTypeAnnotations";
    public static final String ATTR_MethodParameters = "MethodParameters";
    // new in java 9
    public static final String ATTR_Module = "Module";
    public static final String ATTR_ModulePackages = "ModulePackages";
    public static final String ATTR_ModuleMainClass = "ModuleMainClass";
    // new in java 11
    public static final String ATTR_NestHost = "NestHost";
    public static final String ATTR_NestMembers = "NestMembers";
    // source debug for kotlin
    public static final String ATTR_KotlinSourceDebugExtension = "SourceDebugExtension";

    public static final int REF_getField                = 1;
    public static final int REF_getStatic               = 2;
    public static final int REF_putField                = 3;
    public static final int REF_putStatic               = 4;
    public static final int REF_invokeVirtual           = 5;
    public static final int REF_invokeStatic            = 6;
    public static final int REF_invokeSpecial           = 7;
    public static final int REF_newInvokeSpecial        = 8;
    public static final int REF_invokeInterface         = 9;



    // List of known attributes
    public static final String[] KNOWN_ATTRS = {
        ATTR_Code,
        ATTR_ConstantValue,
        ATTR_Exceptions,
        ATTR_LineNumberTable,
        ATTR_SourceFile,
        ATTR_LocalVariableTable,
        ATTR_InnerClasses,
        ATTR_Synthetic,
        ATTR_Deprecated,
        ATTR_Signature,
        ATTR_LocalVariableTypeTable,
        ATTR_EnclosingMethod,
        ATTR_AnnotationDefault,
        ATTR_RuntimeVisibleAnnotations,
        ATTR_RuntimeInvisibleAnnotations,
        ATTR_RuntimeVisibleParameterAnnotations,
        ATTR_RuntimeInvisibleParameterAnnotations,
        ATTR_BootstrapMethods,
        ATTR_Bridge,
        ATTR_Enum,
        ATTR_StackMapTable,
        ATTR_Varargs,
        ATTR_MethodParameters,
        ATTR_Module,
        ATTR_ModulePackages,
        ATTR_ModuleMainClass,
        ATTR_NestHost,
        ATTR_NestMembers,
    };

    // List of required attributes
    public static final String[] REQUIRED_ATTRS = {
        ATTR_Code,
        ATTR_ConstantValue,
        ATTR_Exceptions,
        ATTR_InnerClasses,
        ATTR_Synthetic,
        ATTR_Signature,
        ATTR_RuntimeVisibleAnnotations,
        ATTR_EnclosingMethod,
        ATTR_AnnotationDefault,
        ATTR_RuntimeInvisibleParameterAnnotations,
        ATTR_RuntimeInvisibleAnnotations,
        ATTR_RuntimeInvisibleTypeAnnotations,
        ATTR_RuntimeVisibleAnnotations,
        ATTR_RuntimeVisibleTypeAnnotations,
        ATTR_RuntimeVisibleParameterAnnotations,
        ATTR_StackMapTable,
        ATTR_BootstrapMethods,
        ATTR_Module,
        ATTR_ModulePackages,
        ATTR_ModuleMainClass,
        ATTR_NestHost,
        ATTR_NestMembers,
    };
}
