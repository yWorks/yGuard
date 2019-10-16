/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2003 yWorks GmbH (yguard@yworks.com)
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

package com.yworks.yguard.obf;

public class NoSuchMappingException extends java.lang.IllegalArgumentException
{
  
  private String key;
  /**
   * Creates a new instance of <code>NoSuchMappingException</code> without detail message.
   */
  public NoSuchMappingException(String key)
  {
    super("No mapping found for: "+key);
    this.key = key;
  }
  
  
  /**
   * Constructs an instance of <code>NoSuchMappingException</code> with the specified detail message.
   * @param msg the detail message.
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
