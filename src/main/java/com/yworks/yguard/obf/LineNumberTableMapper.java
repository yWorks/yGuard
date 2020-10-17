package com.yworks.yguard.obf;

import com.yworks.yguard.obf.classfile.LineNumberTableAttrInfo;

import java.io.PrintWriter;

/**
 * The interface Line number table mapper.
 */
public interface LineNumberTableMapper {
  /**
   * Callback method that can be used to remap a line number table.
   *
   * @param className       the classes name that contains the method
   * @param methodName      the name of the method
   * @param methodSignature the signature of the method
   * @param lineNumberTable the table that may be modified by this method
   * @return whether the line number table should be kept
   * @see com.yworks.yguard.obf.YGuardRule#TYPE_LINE_NUMBER_MAPPER com.yworks.yguard.obf.YGuardRule#TYPE_LINE_NUMBER_MAPPERcom.yworks.yguard.obf.YGuardRule#TYPE_LINE_NUMBER_MAPPER
   */
  boolean mapLineNumberTable( String className, String methodName, String methodSignature, LineNumberTableAttrInfo lineNumberTable );

  /**
   * Callback method that can be used to log custom properties to the Printwriter.
   *
   * @param pw the PrintWriter to print to.
   */
  void logProperties( PrintWriter pw );
}
