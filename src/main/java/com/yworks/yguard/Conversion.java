/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard;

import com.yworks.yguard.obf.ClassTree;
import java.util.StringTokenizer;

/**
 * The type Conversion.
 *
 * @author muellese
 */
public class Conversion
{

  /**
   * Creates a new instance of Conversion
   */
  protected Conversion(){}

  /**
   * To java class string.
   *
   * @param className the class name
   * @return the string
   */
  public static String toJavaClass(String className){
      if (className.endsWith(".class")){
        className = className.substring(0, className.length()- 6);
      }
      return className.replace('/','.');
  }


  /**
   * To java type string.
   *
   * @param type the type
   * @return the string
   */
  public static String toJavaType(String type){
      StringBuffer nat = new StringBuffer(30);
      int arraydim = 0;
      while (type.charAt(arraydim)=='[') arraydim++;
      type = type.substring(arraydim);
      switch (type.charAt(0)){
        default:
          throw new IllegalArgumentException("unknown native type:"+type);
        case 'B':
          nat.append("byte");
          break;
        case 'C':
          nat.append("char");
          break;
        case 'D':
          nat.append("double");
          break;
        case 'F':
          nat.append("float");
          break;
        case 'I':
          nat.append("int");
          break;
        case 'J':
          nat.append("long");
          break;
        case 'S':
          nat.append("short");
          break;
        case 'Z':
          nat.append("boolean");
          break;
        case 'V':
          nat.append("void");
          break;
        case 'L':
          String className = type.substring(1, type.length()-1);
          if (className.indexOf('<') >= 0){
            String parameters = type.substring(className.indexOf('<') + 2, className.lastIndexOf('>') - 1);
            className = className.substring(0, className.indexOf('<') );
            nat.append(className.replace('/','.'));
            nat.append('<');
            nat.append(toJavaParameters(parameters));
            nat.append('>');
          } else {
            nat.append(className.replace('/','.'));
          }
          break;
      }
      for (int i = 0; i < arraydim; i++){
          nat.append("[]");
      }
      return nat.toString();
  }

  /**
   * Mapping for signatures (used for generics in 1.5).
   *
   * @param signature the signature
   * @return the string
   * @see com.yworks.yguard.obf.classfile.NameMapper#mapSignature com.yworks.yguard.obf.classfile.NameMapper#mapSignaturecom.yworks.yguard.obf.classfile.NameMapper#mapSignature
   */
  public static String mapSignature(String signature){
      return new ClassTree().mapSignature(signature);
    }


  /**
   * To java parameters string.
   *
   * @param parameters the parameters
   * @return the string
   */
  public static String toJavaParameters(String parameters){
    StringBuffer nat = new StringBuffer(30);
    switch (parameters.charAt(0)){
        default:
          throw new IllegalArgumentException("unknown native type:"+parameters.charAt(0));
        case '+':
          nat.append("? extends ").append(toJavaParameters(parameters.substring(1)));
          break;
        case '-':
          nat.append("? super ").append(toJavaParameters(parameters.substring(1)));
          break;
        case '*':
          nat.append("*");
          if (parameters.length() > 1){
            nat.append(", ").append(toJavaParameters(parameters.substring(1)));
          }
          break;
        case 'B':
          nat.append("byte");
          break;
        case 'C':
          nat.append("char");
          break;
        case 'D':
          nat.append("double");
          break;
        case 'F':
          nat.append("float");
          break;
        case 'I':
          nat.append("int");
          break;
        case 'J':
          nat.append("long");
          break;
        case 'S':
          nat.append("short");
          break;
        case 'Z':
          nat.append("boolean");
          break;
        case 'V':
          nat.append("void");
          break;
        case 'L':
          int len = parameters.indexOf('<');
          if (len >= 0){
            len = Math.min(len, parameters.indexOf(';'));
          }
          break;
        case 'T':
          int index = parameters.indexOf(';');
          nat.append(parameters.substring(1, index));
          if (parameters.length() > index){
            nat.append(", ");
            nat.append(parameters.substring(index));
          }
          break;
    }
    return nat.toString();
  }

  /**
   * To java method string.
   *
   * @param name      the name
   * @param signature the signature
   * @return the string
   */
  public static String toJavaMethod(String name, String signature){
    String argsonly = signature.substring(signature.indexOf('(')+1);
    String ret = signature.substring(signature.indexOf(')')+1);
    ret = toJavaType(ret);
    StringBuffer args = new StringBuffer();
    args.append('(');
    if (argsonly.indexOf(')')>0){
      argsonly = argsonly.substring(0,argsonly.indexOf(')'));
      toJavaArguments(argsonly, args);
    }
    args.append(')');
    return ret+" "+name+args.toString();
  }

  /**
   * To java arguments string.
   *
   * @param args the args
   * @return the string
   */
  public static String toJavaArguments(String args){
    StringBuffer b= new StringBuffer(args.length() + 32);
    toJavaArguments(args, b);
    return b.toString();
  }

  private static void toJavaArguments(String argsonly, StringBuffer args) {
    int argcount = 0;
    int pos = 0;
    StringBuffer arg = new StringBuffer(20);
    while (pos < argsonly.length()){
      while (argsonly.charAt(pos) == '['){
        arg.append('[');
        pos++;
      }
      if (argsonly.charAt(pos) == 'L'){
        while (argsonly.charAt(pos) != ';'){
          arg.append(argsonly.charAt(pos));
          pos++;
        }
        arg.append(';');
        if (argcount > 0){
          args.append(',');
          args.append(' ');
        }
        args.append(toJavaType(arg.toString()));
        argcount++;
        arg.setLength(0);
        pos++;
      } else {
        arg.append(argsonly.charAt(pos));
        if (argcount > 0){
          args.append(',');
          args.append(' ');
        }
        args.append(toJavaType(arg.toString()));
        argcount++;
        arg.setLength(0);
        pos++;
      }
    }
  }
}
