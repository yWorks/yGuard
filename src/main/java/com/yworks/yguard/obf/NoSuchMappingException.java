/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2003 yWorks GmbH (yguard@yworks.com)
 *
 */

package com.yworks.yguard.obf;

public class NoSuchMappingException extends java.lang.IllegalArgumentException
{
  
  private String key;
  /**
   * Constructs an instance of <code>NoSuchMappingException</code> with the specified detail message.
   * @param key the detail message.
   */
  public NoSuchMappingException(String key)
  {
    super("No mapping found for: "+key);
    this.key = key;
  }

  /**
   * Creates a new instance of <code>NoSuchMappingException</code> without detail message.
   */
  public NoSuchMappingException()
  {
    super("No mapping found!");
    this.key = "";
  }
  
  public String getKey(){
    return key;
  }
}
