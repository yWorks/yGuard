/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
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
 * The author may be contacted at markw@retrologic.com 
 *
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf;

import java.io.*;
import java.util.*;

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
