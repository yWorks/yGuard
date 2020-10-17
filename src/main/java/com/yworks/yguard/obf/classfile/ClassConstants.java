/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 *

 *
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf.classfile;

/**
 * Constants used in representing a Java class-file (*.class).
 *
 * @author Mark Welsh
 */
public interface ClassConstants {
  /**
   * The constant MAGIC.
   */
// Constants -------------------------------------------------------------
  int MAGIC = 0xCAFEBABE;

  /**
   * The constant MINOR_VERSION_MAX.
   */
  int MINOR_VERSION_MAX = 3;
  /**
   * The constant MAJOR_VERSION.
   */
  int MAJOR_VERSION = 0x3A;

  /**
   * The constant ACC_PUBLIC.
   */
  int ACC_PUBLIC = 0x0001;
  /**
   * The constant ACC_PRIVATE.
   */
  int ACC_PRIVATE = 0x0002;
  /**
   * The constant ACC_PROTECTED.
   */
  int ACC_PROTECTED = 0x0004;
  /**
   * The constant ACC_STATIC.
   */
  int ACC_STATIC = 0x0008;
  /**
   * The constant ACC_FINAL.
   */
  int ACC_FINAL = 0x0010;
  /**
   * The constant ACC_SUPER.
   */
  int ACC_SUPER = 0x0020;
  /**
   * The constant ACC_SYNCHRONIZED.
   */
  int ACC_SYNCHRONIZED = 0x0020;
  /**
   * The constant ACC_VOLATILE.
   */
  int ACC_VOLATILE = 0x0040;
  /**
   * The constant ACC_TRANSIENT.
   */
  int ACC_TRANSIENT = 0x0080;
  /**
   * The constant ACC_NATIVE.
   */
  int ACC_NATIVE = 0x0100;
  /**
   * The constant ACC_INTERFACE.
   */
  int ACC_INTERFACE = 0x0200;
  /**
   * The constant ACC_ABSTRACT.
   */
  int ACC_ABSTRACT = 0x0400;
  /**
   * The constant ACC_SYNTHETIC.
   */
  int ACC_SYNTHETIC = 0x1000;
  /**
   * The constant ACC_ANNOTATION.
   */
  int ACC_ANNOTATION = 0x2000;
  /**
   * The constant ACC_ENUM.
   */
  int ACC_ENUM = 0x4000;
  /**
   * The constant ACC_BRIDGE.
   */
  int ACC_BRIDGE = 0x0040;
  /**
   * The constant ACC_VARARGS.
   */
  int ACC_VARARGS = 0x0080;

  /**
   * The constant CONSTANT_Utf8.
   */
  int CONSTANT_Utf8 = 1;
  /**
   * The constant CONSTANT_Integer.
   */
  int CONSTANT_Integer = 3;
  /**
   * The constant CONSTANT_Float.
   */
  int CONSTANT_Float = 4;
  /**
   * The constant CONSTANT_Long.
   */
  int CONSTANT_Long = 5;
  /**
   * The constant CONSTANT_Double.
   */
  int CONSTANT_Double = 6;
  /**
   * The constant CONSTANT_Class.
   */
  int CONSTANT_Class = 7;
  /**
   * The constant CONSTANT_String.
   */
  int CONSTANT_String = 8;
  /**
   * The constant CONSTANT_Fieldref.
   */
  int CONSTANT_Fieldref = 9;
  /**
   * The constant CONSTANT_Methodref.
   */
  int CONSTANT_Methodref = 10;
  /**
   * The constant CONSTANT_InterfaceMethodref.
   */
  int CONSTANT_InterfaceMethodref = 11;
  /**
   * The constant CONSTANT_NameAndType.
   */
  int CONSTANT_NameAndType = 12;
  /**
   * The constant CONSTANT_MethodHandle.
   */
// new in java 7
  int CONSTANT_MethodHandle = 15;
  /**
   * The constant CONSTANT_MethodType.
   */
// new in java 7
  int CONSTANT_MethodType = 16;
  /**
   * The constant CONSTANT_Dynamic.
   */
// new in java 11
  int CONSTANT_Dynamic = 17;
  /**
   * The constant CONSTANT_InvokeDynamic.
   */
// new in java 7
  int CONSTANT_InvokeDynamic = 18;
  /**
   * The constant CONSTANT_Module.
   */
// new in java 9
  int CONSTANT_Module = 19;
  /**
   * The constant CONSTANT_Package.
   */
// new in java 9
  int CONSTANT_Package = 20;

  /**
   * The constant ATTR_Unknown.
   */
  String ATTR_Unknown = "Unknown";
  /**
   * The constant ATTR_Code.
   */
  String ATTR_Code = "Code";
  /**
   * The constant ATTR_ConstantValue.
   */
  String ATTR_ConstantValue = "ConstantValue";
  /**
   * The constant ATTR_Exceptions.
   */
  String ATTR_Exceptions = "Exceptions";
  /**
   * The constant ATTR_StackMapTable.
   */
  String ATTR_StackMapTable = "StackMapTable";
  /**
   * The constant ATTR_LineNumberTable.
   */
  String ATTR_LineNumberTable = "LineNumberTable";
  /**
   * The constant ATTR_SourceFile.
   */
  String ATTR_SourceFile = "SourceFile";
  /**
   * The constant ATTR_SourceDebug.
   */
  String ATTR_SourceDebug = "SourceDebug";
  /**
   * The constant ATTR_LocalVariableTable.
   */
  String ATTR_LocalVariableTable = "LocalVariableTable";
  /**
   * The constant ATTR_InnerClasses.
   */
  String ATTR_InnerClasses = "InnerClasses";
  /**
   * The constant ATTR_Synthetic.
   */
  String ATTR_Synthetic = "Synthetic";
  /**
   * The constant ATTR_Deprecated.
   */
  String ATTR_Deprecated = "Deprecated";
  /**
   * The constant ATTR_LocalVariableTypeTable.
   */
  String ATTR_LocalVariableTypeTable = "LocalVariableTypeTable";
  /**
   * The constant ATTR_Signature.
   */
  String ATTR_Signature = "Signature";
  /**
   * The constant ATTR_EnclosingMethod.
   */
  String ATTR_EnclosingMethod = "EnclosingMethod";
  /**
   * The constant ATTR_RuntimeVisibleAnnotations.
   */
  String ATTR_RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";
  /**
   * The constant ATTR_RuntimeInvisibleAnnotations.
   */
  String ATTR_RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations";
  /**
   * The constant ATTR_RuntimeVisibleParameterAnnotations.
   */
  String ATTR_RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations";
  /**
   * The constant ATTR_RuntimeInvisibleParameterAnnotations.
   */
  String ATTR_RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";
  /**
   * The constant ATTR_AnnotationDefault.
   */
  String ATTR_AnnotationDefault = "AnnotationDefault";
  /**
   * The constant ATTR_Bridge.
   */
  String ATTR_Bridge = "Bridge";
  /**
   * The constant ATTR_Enum.
   */
  String ATTR_Enum = "Enum";
  /**
   * The constant ATTR_Varargs.
   */
  String ATTR_Varargs = "Varargs";
  /**
   * The constant ATTR_BootstrapMethods.
   */
// new in java 7
  String ATTR_BootstrapMethods = "BootstrapMethods";
  /**
   * The constant ATTR_RuntimeVisibleTypeAnnotations.
   */
// new in java 8
  String ATTR_RuntimeVisibleTypeAnnotations = "RuntimeVisibleTypeAnnotations";
  /**
   * The constant ATTR_RuntimeInvisibleTypeAnnotations.
   */
  String ATTR_RuntimeInvisibleTypeAnnotations = "RuntimeInvisibleTypeAnnotations";
  /**
   * The constant ATTR_MethodParameters.
   */
  String ATTR_MethodParameters = "MethodParameters";
  /**
   * The constant ATTR_Module.
   */
// new in java 9
  String ATTR_Module = "Module";
  /**
   * The constant ATTR_ModulePackages.
   */
  String ATTR_ModulePackages = "ModulePackages";
  /**
   * The constant ATTR_ModuleMainClass.
   */
  String ATTR_ModuleMainClass = "ModuleMainClass";
  /**
   * The constant ATTR_NestHost.
   */
// new in java 11
  String ATTR_NestHost = "NestHost";
  /**
   * The constant ATTR_NestMembers.
   */
  String ATTR_NestMembers = "NestMembers";
  /**
   * The constant ATTR_SourceDebugExtension.
   */
// source debug for kotlin
  String ATTR_SourceDebugExtension = "SourceDebugExtension";

  /**
   * The constant REF_getField.
   */
  int REF_getField = 1;
  /**
   * The constant REF_getStatic.
   */
  int REF_getStatic = 2;
  /**
   * The constant REF_putField.
   */
  int REF_putField = 3;
  /**
   * The constant REF_putStatic.
   */
  int REF_putStatic = 4;
  /**
   * The constant REF_invokeVirtual.
   */
  int REF_invokeVirtual = 5;
  /**
   * The constant REF_invokeStatic.
   */
  int REF_invokeStatic = 6;
  /**
   * The constant REF_invokeSpecial.
   */
  int REF_invokeSpecial = 7;
  /**
   * The constant REF_newInvokeSpecial.
   */
  int REF_newInvokeSpecial = 8;
  /**
   * The constant REF_invokeInterface.
   */
  int REF_invokeInterface = 9;


  /**
   * The constant KNOWN_ATTRS.
   */
// List of known attributes
  String[] KNOWN_ATTRS = {
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

  /**
   * The constant REQUIRED_ATTRS.
   */
// List of required attributes
  String[] REQUIRED_ATTRS = {
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
