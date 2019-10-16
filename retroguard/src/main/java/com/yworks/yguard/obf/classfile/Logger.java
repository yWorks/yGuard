/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author may be contacted at yguard@yworks.com 
 *
 * Java and all Java-based marks are trademarks or registered 
 * trademarks of Sun Microsystems, Inc. in the U.S. and other countries.
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
