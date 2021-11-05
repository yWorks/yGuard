/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard;

/**
 * The type Parse exception.
 *
 * @author Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public class ParseException extends java.lang.RuntimeException
{

  /**
   * Creates a new instance of <code>ParseException</code> without detail message.
   */
  public ParseException()
  {
  }


  /**
   * Constructs an instance of <code>ParseException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public ParseException(String msg)
  {
    super(msg);
  }

  /**
   * Instantiates a new Parse exception.
   *
   * @param cause the cause
   */
  public ParseException( Exception cause ) {
    super(cause);
  }
}
