/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

/**
 *
 * @author  Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public interface Filter
{
  boolean accepts(Object o);
}
