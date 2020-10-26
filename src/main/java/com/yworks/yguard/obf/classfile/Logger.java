/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

 */
package com.yworks.yguard.obf.classfile;

import java.io.PrintStream;

/**
 * The type Logger.
 *
 * @author muellese
 */
public class Logger
{
  private static Logger instance;
  private PrintStream out;
  private PrintStream err;

  private boolean allResolved = true;
  
  static{
    new Logger(System.out, System.err);
  }

  /**
   * Get instance logger.
   *
   * @return the logger
   */
  public static Logger getInstance(){
    return instance;
  }

  /**
   * Creates a new instance of Logger
   */
  protected Logger()
  {
    instance = this;
  }

  /**
   * Instantiates a new Logger.
   *
   * @param out the out
   * @param err the err
   */
  protected Logger(PrintStream out, PrintStream err){
    instance = this;
    this.out = out;
    this.err = err;
  }

  /**
   * Error.
   *
   * @param message the message
   */
  public void error(String message){
    err.println(message);
  }

  /**
   * Log.
   *
   * @param message the message
   */
  public void log(String message){
    out.println(message);
  }

  /**
   * Warning.
   *
   * @param message the message
   */
  public void warning(String message){
    err.println(message);
  }

  /**
   * Warning to logfile.
   *
   * @param message the message
   */
  public void warningToLogfile(String message) {}

  /**
   * Sets unresolved.
   */
  public void setUnresolved() {
    this.allResolved = false;
  }

  /**
   * Is all resolved boolean.
   *
   * @return the boolean
   */
  public boolean isAllResolved() {
    return allResolved;
  }

}
