/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf;

/**
 * A 'cons' of two references -- useful as a generic return grouping from Enumerations.
 *
 * @author      Mark Welsh
 */
public class Cons
{
    // Fields ----------------------------------------------------------------
    public Object car;
    public Object cdr;


    // Instance Methods ---------------------------------------------------------
    /** Ctor. */
    public Cons(Object car, Object cdr)
    {
        this.car = car;
        this.cdr = cdr;
    }
}
