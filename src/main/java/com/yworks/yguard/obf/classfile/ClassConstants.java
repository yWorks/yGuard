/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf.classfile;

/**
 * Constants used in representing a Java class-file (*.class).
 *
 * @author Mark Welsh
 */
public interface  ClassConstants
{
  // Constants -------------------------------------------------------------
  /**
   * The constant MAGIC.
   */
  public static final int MAGIC = 0xCAFEBABE;

  /**
   * The constant MINOR_VERSION_MAX.
   */
  public static final int MINOR_VERSION_MAX = 3;
  /**
   * The constant MAJOR_VERSION.
   */
  public static final int MAJOR_VERSION     = 0x3C;

  /**
   * The constant ACC_PUBLIC.
   */
  public static final int ACC_PUBLIC      = 0x0001;
  /**
   * The constant ACC_PRIVATE.
   */
  public static final int ACC_PRIVATE     = 0x0002;
  /**
   * The constant ACC_PROTECTED.
   */
  public static final int ACC_PROTECTED   = 0x0004;
  /**
   * The constant ACC_STATIC.
   */
  public static final int ACC_STATIC      = 0x0008;
  /**
   * The constant ACC_FINAL.
   */
  public static final int ACC_FINAL       = 0x0010;
  /**
   * The constant ACC_SUPER.
   */
  public static final int ACC_SUPER       = 0x0020;
  /**
   * The constant ACC_SYNCHRONIZED.
   */
  public static final int ACC_SYNCHRONIZED= 0x0020;
  /**
   * The constant ACC_VOLATILE.
   */
  public static final int ACC_VOLATILE    = 0x0040;
  /**
   * The constant ACC_TRANSIENT.
   */
  public static final int ACC_TRANSIENT   = 0x0080;
  /**
   * The constant ACC_NATIVE.
   */
  public static final int ACC_NATIVE      = 0x0100;
  /**
   * The constant ACC_INTERFACE.
   */
  public static final int ACC_INTERFACE   = 0x0200;
  /**
   * The constant ACC_ABSTRACT.
   */
  public static final int ACC_ABSTRACT    = 0x0400;
  /**
   * The constant ACC_SYNTHETIC.
   */
  public static final int ACC_SYNTHETIC   = 0x1000;
  /**
   * The constant ACC_ANNOTATION.
   */
  public static final int ACC_ANNOTATION  = 0x2000;
  /**
   * The constant ACC_ENUM.
   */
  public static final int ACC_ENUM        = 0x4000;
  /**
   * The constant ACC_BRIDGE.
   */
  public static final int ACC_BRIDGE      = 0x0040;
  /**
   * The constant ACC_VARARGS.
   */
  public static final int ACC_VARARGS     = 0x0080;

  /**
   * The constant CONSTANT_Utf8.
   */
  public static final int CONSTANT_Utf8               = 1;
  /**
   * The constant CONSTANT_Integer.
   */
  public static final int CONSTANT_Integer            = 3;
  /**
   * The constant CONSTANT_Float.
   */
  public static final int CONSTANT_Float              = 4;
  /**
   * The constant CONSTANT_Long.
   */
  public static final int CONSTANT_Long               = 5;
  /**
   * The constant CONSTANT_Double.
   */
  public static final int CONSTANT_Double             = 6;
  /**
   * The constant CONSTANT_Class.
   */
  public static final int CONSTANT_Class              = 7;
  /**
   * The constant CONSTANT_String.
   */
  public static final int CONSTANT_String             = 8;
  /**
   * The constant CONSTANT_Fieldref.
   */
  public static final int CONSTANT_Fieldref           = 9;
  /**
   * The constant CONSTANT_Methodref.
   */
  public static final int CONSTANT_Methodref          = 10;
  /**
   * The constant CONSTANT_InterfaceMethodref.
   */
  public static final int CONSTANT_InterfaceMethodref = 11;
  /**
   * The constant CONSTANT_NameAndType.
   */
  public static final int CONSTANT_NameAndType        = 12;

  // new in java 7
  /**
   * The constant CONSTANT_MethodHandle.
   */
  public static final int CONSTANT_MethodHandle       = 15;
  // new in java 7
  /**
   * The constant CONSTANT_MethodType.
   */
  public static final int CONSTANT_MethodType         = 16;

  // new in java 11
  /**
   * The constant CONSTANT_Dynamic.
   */
  public static final int CONSTANT_Dynamic            = 17;

  // new in java 7
  /**
   * The constant CONSTANT_InvokeDynamic.
   */
  public static final int CONSTANT_InvokeDynamic      = 18;

  // new in java 9
  /**
   * The constant CONSTANT_Module.
   */
  public static final int CONSTANT_Module             = 19;
  // new in java 9
  /**
   * The constant CONSTANT_Package.
   */
  public static final int CONSTANT_Package            = 20;

  /**
   * The constant ATTR_Unknown.
   */
  public static final String ATTR_Unknown             = "Unknown";
  /**
   * The constant ATTR_Code.
   */
  public static final String ATTR_Code                = "Code";
  /**
   * The constant ATTR_ConstantValue.
   */
  public static final String ATTR_ConstantValue       = "ConstantValue";
  /**
   * The constant ATTR_Exceptions.
   */
  public static final String ATTR_Exceptions          = "Exceptions";
  /**
   * The constant ATTR_StackMapTable.
   */
  public static final String ATTR_StackMapTable       = "StackMapTable";
  /**
   * The constant ATTR_LineNumberTable.
   */
  public static final String ATTR_LineNumberTable     = "LineNumberTable";
  /**
   * The constant ATTR_SourceFile.
   */
  public static final String ATTR_SourceFile          = "SourceFile";
  /**
   * The constant ATTR_SourceDebug.
   */
  public static final String ATTR_SourceDebug         = "SourceDebug";
  /**
   * The constant ATTR_LocalVariableTable.
   */
  public static final String ATTR_LocalVariableTable  = "LocalVariableTable";
  /**
   * The constant ATTR_InnerClasses.
   */
  public static final String ATTR_InnerClasses        = "InnerClasses";
  /**
   * The constant ATTR_Synthetic.
   */
  public static final String ATTR_Synthetic           = "Synthetic";
  /**
   * The constant ATTR_Deprecated.
   */
  public static final String ATTR_Deprecated          = "Deprecated";
  /**
   * The constant ATTR_LocalVariableTypeTable.
   */
  public static final String ATTR_LocalVariableTypeTable = "LocalVariableTypeTable";
  /**
   * The constant ATTR_Signature.
   */
  public static final String ATTR_Signature           = "Signature";
  /**
   * The constant ATTR_EnclosingMethod.
   */
  public static final String ATTR_EnclosingMethod     = "EnclosingMethod";
  /**
   * The constant ATTR_RuntimeVisibleAnnotations.
   */
  public static final String ATTR_RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";
  /**
   * The constant ATTR_RuntimeInvisibleAnnotations.
   */
  public static final String ATTR_RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations";
  /**
   * The constant ATTR_RuntimeVisibleParameterAnnotations.
   */
  public static final String ATTR_RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations";
  /**
   * The constant ATTR_RuntimeInvisibleParameterAnnotations.
   */
  public static final String ATTR_RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";
  /**
   * The constant ATTR_AnnotationDefault.
   */
  public static final String ATTR_AnnotationDefault   = "AnnotationDefault";
  /**
   * The constant ATTR_Bridge.
   */
  public static final String ATTR_Bridge = "Bridge";
  /**
   * The constant ATTR_Enum.
   */
  public static final String ATTR_Enum = "Enum";
  /**
   * The constant ATTR_Varargs.
   */
  public static final String ATTR_Varargs = "Varargs";

  // new in java 7
  /**
   * The constant ATTR_BootstrapMethods.
   */
  public static final String ATTR_BootstrapMethods = "BootstrapMethods";

  // new in java 8
  /**
   * The constant ATTR_RuntimeVisibleTypeAnnotations.
   */
  public static final String ATTR_RuntimeVisibleTypeAnnotations = "RuntimeVisibleTypeAnnotations";
  /**
   * The constant ATTR_RuntimeInvisibleTypeAnnotations.
   */
  public static final String ATTR_RuntimeInvisibleTypeAnnotations = "RuntimeInvisibleTypeAnnotations";
  /**
   * The constant ATTR_MethodParameters.
   */
  public static final String ATTR_MethodParameters = "MethodParameters";

  // new in java 9
  /**
   * The constant ATTR_Module.
   */
  public static final String ATTR_Module = "Module";
  /**
   * The constant ATTR_ModulePackages.
   */
  public static final String ATTR_ModulePackages = "ModulePackages";
  /**
   * The constant ATTR_ModuleMainClass.
   */
  public static final String ATTR_ModuleMainClass = "ModuleMainClass";

  // new in java 11
  /**
   * The constant ATTR_NestHost.
   */
  public static final String ATTR_NestHost = "NestHost";
  /**
   * The constant ATTR_NestMembers.
   */
  public static final String ATTR_NestMembers = "NestMembers";

  // source debug for kotlin
  /**
   * The constant ATTR_SourceDebugExtension.
   */
  public static final String ATTR_SourceDebugExtension = "SourceDebugExtension";

  // new in java 16
  public static final String ATTR_Record = "Record";

  /**
   * The constant REF_getField.
   */
  public static final int REF_getField                = 1;
  /**
   * The constant REF_getStatic.
   */
  public static final int REF_getStatic               = 2;
  /**
   * The constant REF_putField.
   */
  public static final int REF_putField                = 3;
  /**
   * The constant REF_putStatic.
   */
  public static final int REF_putStatic               = 4;
  /**
   * The constant REF_invokeVirtual.
   */
  public static final int REF_invokeVirtual           = 5;
  /**
   * The constant REF_invokeStatic.
   */
  public static final int REF_invokeStatic            = 6;
  /**
   * The constant REF_invokeSpecial.
   */
  public static final int REF_invokeSpecial           = 7;
  /**
   * The constant REF_newInvokeSpecial.
   */
  public static final int REF_newInvokeSpecial        = 8;
  /**
   * The constant REF_invokeInterface.
   */
  public static final int REF_invokeInterface         = 9;


  /**
   * The list of known attributes.
   */
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

  /**
   * The list of required attributes.
   */
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
    ATTR_Record,
  };
}
