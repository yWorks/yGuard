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
 *
 * @author  muellese
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
  
  public static Logger getInstance(){
    return instance;
  }
  
  /** Creates a new instance of Logger */
  protected Logger()
  {
    instance = this;
  }
  
  protected Logger(PrintStream out, PrintStream err){
    instance = this;
    this.out = out;
    this.err = err;
  }
  
  public void error(String message){
    err.println(message);
  }
  
  public void log(String message){
    out.println(message);
  }
  
  public void warning(String message){
    err.println(message);
  }

  public void warningToLogfile(String message) {}

  public void setUnresolved() {
    this.allResolved = false;
  }

  public boolean isAllResolved() {
    return allResolved;
  }

}
