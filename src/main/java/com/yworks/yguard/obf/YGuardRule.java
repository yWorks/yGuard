/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

import java.io.*;
import java.lang.reflect.Modifier;

/**
 * Representation of RGS script files entry.
 *
 * @author Mark Welsh
 */
public class YGuardRule
{
  // Constants -------------------------------------------------------------
  /**
   * The constant PUBLIC.
   */
  public static final int PUBLIC = Modifier.PUBLIC;
  /**
   * The constant PROTECTED.
   */
  public static final int PROTECTED = Modifier.PROTECTED;
  /**
   * The constant FRIENDLY.
   */
  public static final int FRIENDLY = 4096;
  /**
   * The constant PRIVATE.
   */
  public static final int PRIVATE = Modifier.PRIVATE;

  /**
   * The constant LEVEL_NONE.
   */
  public static final int LEVEL_NONE = 0;
  /**
   * The constant LEVEL_PUBLIC.
   */
  public static final int LEVEL_PUBLIC = PUBLIC;
  /**
   * The constant LEVEL_PROTECTED.
   */
  public static final int LEVEL_PROTECTED = PROTECTED | LEVEL_PUBLIC;
  /**
   * The constant LEVEL_FRIENDLY.
   */
  public static final int LEVEL_FRIENDLY = FRIENDLY | LEVEL_PROTECTED;
  /**
   * The constant LEVEL_PRIVATE.
   */
  public static final int LEVEL_PRIVATE = PRIVATE | LEVEL_FRIENDLY;

  /**
   * The constant TYPE_ATTR.
   */
  public static final int TYPE_ATTR = 0;
  /**
   * The constant TYPE_CLASS.
   */
  public static final int TYPE_CLASS = 1;
  /**
   * The constant TYPE_FIELD.
   */
  public static final int TYPE_FIELD = 2;
  /**
   * The constant TYPE_METHOD.
   */
  public static final int TYPE_METHOD = 3;
  /**
   * The constant TYPE_PACKAGE_MAP.
   */
  public static final int TYPE_PACKAGE_MAP = 4;
  /**
   * The constant TYPE_CLASS_MAP.
   */
  public static final int TYPE_CLASS_MAP = 5;
  /**
   * The constant TYPE_FIELD_MAP.
   */
  public static final int TYPE_FIELD_MAP = 6;
  /**
   * The constant TYPE_METHOD_MAP.
   */
  public static final int TYPE_METHOD_MAP = 7;
  /**
   * The constant TYPE_SOURCE_ATTRIBUTE_MAP.
   */
  public static final int TYPE_SOURCE_ATTRIBUTE_MAP = 8;
  /**
   * The constant TYPE_LINE_NUMBER_MAPPER.
   */
  public static final int TYPE_LINE_NUMBER_MAPPER = 9;
  /**
   * The constant TYPE_ATTR2.
   */
  public static final int TYPE_ATTR2 = 10;
  /**
   * The constant TYPE_PACKAGE.
   */
  public static final int TYPE_PACKAGE = 11;

  // Fields ----------------------------------------------------------------
  /**
   * The Type.
   */
  public int type;
  /**
   * The Name.
   */
  public String name;
  /**
   * The Descriptor.
   */
  public String descriptor;
  /**
   * The Obf name.
   */
  public String obfName;
  /**
   * The Line number table mapper.
   */
  public LineNumberTableMapper lineNumberTableMapper;
  /**
   * The Retain fields.
   */
  public int retainFields = LEVEL_NONE;
  /**
   * The Retain methods.
   */
  public int retainMethods = LEVEL_NONE;
  /**
   * The Retain classes.
   */
  public int retainClasses = LEVEL_PRIVATE;

  /**
   * Instantiates a new Y guard rule.
   *
   * @param type the type
   * @param name the name
   */
  public YGuardRule(int type, String name)
  {
    this.type = type;
    this.name = name;
    this.descriptor = null;
    this.obfName = null;
    this.lineNumberTableMapper = null;
  }

  /**
   * Instantiates a new Y guard rule.
   *
   * @param type       the type
   * @param name       the name
   * @param descriptor the descriptor
   */
  public YGuardRule(int type, String name, String descriptor)
  {
      this.obfName = null;
      this.type = type;
      this.name = name;
      this.descriptor = descriptor;
      this.lineNumberTableMapper = null;
  }

  /**
   * Instantiates a new Y guard rule.
   *
   * @param className             the class name
   * @param lineNumberTableMapper the line number table mapper
   */
  public YGuardRule(String className, LineNumberTableMapper lineNumberTableMapper) {
    this.descriptor = null;
    this.obfName = null;
    this.name = className;
    this.type = TYPE_LINE_NUMBER_MAPPER;
    this.lineNumberTableMapper = lineNumberTableMapper;
  }

  // Instance Methods-------------------------------------------------------
  /**
   * Log properties.
   *
   * @param pw the pw
   */
  public void logProperties(PrintWriter pw){
    if (type == TYPE_LINE_NUMBER_MAPPER){
      lineNumberTableMapper.logProperties(pw);
    }
  }

  public String toString()
  {
    return typeToString(type)+" "+ name + " "
    + descriptor+" fields: "+methodToString(retainFields)
    +" methods: "+methodToString(retainMethods)
    +" classes: "+methodToString(retainClasses);
  }

  /**
   * Type to string string.
   *
   * @param type the type
   * @return the string
   */
  public static String typeToString(int type){
    switch (type){
      default: return "Unknown type "+type;
      case TYPE_ATTR:
        return "ATTRIBUTE";
      case TYPE_ATTR2:
        return "ATTRIBUTE PER CLASS";
      case TYPE_CLASS:
        return "CLASS";
      case TYPE_CLASS_MAP:
        return "CLASS_MAP";
      case TYPE_FIELD:
        return "FIELD";
      case TYPE_FIELD_MAP:
        return "FIELD_MAP";
      case TYPE_METHOD:
        return "METHOD";
      case TYPE_METHOD_MAP:
        return "METHOD_MAP";
      case TYPE_PACKAGE_MAP:
        return "PACKAGE_MAP";
      case TYPE_SOURCE_ATTRIBUTE_MAP:
        return "SOURCE_ATTRIBUTE_MAP";
      case TYPE_LINE_NUMBER_MAPPER:
        return "LINE_NUMBER_MAPPER";
      case TYPE_PACKAGE:
        return "PACKAGE";
    }
  }

  /**
   * Method to string string.
   *
   * @param modifier the modifier
   * @return the string
   */
  public static String methodToString(int modifier){
    switch (modifier){
      default: return "Unknown modifier "+modifier;
      case LEVEL_NONE:
        return "LEVEL_NONE";
      case LEVEL_FRIENDLY:
        return "LEVEL_FRIENDLY";
      case LEVEL_PRIVATE:
        return "LEVEL_PRIVATE";
      case LEVEL_PROTECTED:
        return "LEVEL_PROTECTED";
      case LEVEL_PUBLIC:
        return "LEVEL_PUBLIC";
    }
  }
}
