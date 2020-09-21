/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

/**
 * Base interface for name generators for a given namespace.
 *
 * @author      Mark Welsh
 */
public interface NameMaker
{
    /** Return the next unique name for this namespace, differing only for identical arg-lists. */
    public String nextName(String descriptor);
}
