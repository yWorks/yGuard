/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

import java.io.*;
import java.util.*;
import java.lang.reflect.Modifier;

/**
 * Representation of RGS script files entry.
 *
 * @author      Mark Welsh
 */
public class YGuardRule
{
    public static final int PUBLIC = Modifier.PUBLIC;
    public static final int PROTECTED = Modifier.PROTECTED;
    public static final int FRIENDLY = 4096; 
    public static final int PRIVATE = Modifier.PRIVATE;
        
    public static final int LEVEL_NONE = 0;
    public static final int LEVEL_PUBLIC = PUBLIC;
    public static final int LEVEL_PROTECTED = PROTECTED | LEVEL_PUBLIC;
    public static final int LEVEL_FRIENDLY = FRIENDLY | LEVEL_PROTECTED;
    public static final int LEVEL_PRIVATE = PRIVATE | LEVEL_FRIENDLY;

    // Constants -------------------------------------------------------------
    public static final int TYPE_ATTR = 0;
    public static final int TYPE_CLASS = 1;
    public static final int TYPE_FIELD = 2;
    public static final int TYPE_METHOD = 3;
    public static final int TYPE_PACKAGE_MAP = 4;
    public static final int TYPE_CLASS_MAP = 5;
    public static final int TYPE_FIELD_MAP = 6;
    public static final int TYPE_METHOD_MAP = 7;
    public static final int TYPE_SOURCE_ATTRIBUTE_MAP = 8;
    public static final int TYPE_LINE_NUMBER_MAPPER = 9;
    public static final int TYPE_ATTR2 = 10;
    public static final int TYPE_PACKAGE = 11;

    // Fields ----------------------------------------------------------------
    public int type;
    public String name;
    public String descriptor;
    public String obfName;
    public LineNumberTableMapper lineNumberTableMapper;
    public int retainFields = LEVEL_NONE;
    public int retainMethods = LEVEL_NONE;
    public int retainClasses = LEVEL_PRIVATE;

    // Instance Methods-------------------------------------------------------
    public YGuardRule(int type, String name)
    {
      this.type = type;
      this.name = name;
      this.descriptor = null;
      this.obfName = null;
      this.lineNumberTableMapper = null;
    }
    public YGuardRule(int type, String name, String descriptor)
    {
        this.obfName = null;
        this.type = type;
        this.name = name;
        this.descriptor = descriptor;
        this.lineNumberTableMapper = null;
    }

    public YGuardRule(String className, LineNumberTableMapper lineNumberTableMapper) {
      this.descriptor = null;
      this.obfName = null;
      this.name = className;
      this.type = TYPE_LINE_NUMBER_MAPPER;
      this.lineNumberTableMapper = lineNumberTableMapper;
    }

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
