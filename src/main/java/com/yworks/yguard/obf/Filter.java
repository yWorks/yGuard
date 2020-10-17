/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

/**
 * The interface Filter.
 *
 * @author Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public interface Filter
{
    /**
     * Accepts boolean.
     *
     * @param o the o
     * @return the boolean
     */
    boolean accepts(Object o);
}
