/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

/**
 * A 'cons' of two references -- useful as a generic return grouping from Enumerations.
 *
 * @author Mark Welsh
 */
public class Cons
{
    // Fields ----------------------------------------------------------------
    /**
     * The Car.
     */
    public Object car;
    /**
     * The Cdr.
     */
    public Object cdr;


    // Instance Methods ---------------------------------------------------------

    /**
     * Ctor.
     *
     * @param car the car
     * @param cdr the cdr
     */
    public Cons(Object car, Object cdr)
    {
        this.car = car;
        this.cdr = cdr;
    }
}
