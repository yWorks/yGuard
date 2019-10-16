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
package com.yworks.yguard.obf;

import com.yworks.yguard.obf.classfile.*;

/**
 * Tree item representing a field.
 *
 * @author      Mark Welsh
 */
public class Fd extends MdFd
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------


    // Instance Methods ------------------------------------------------------
    /** Ctor. */
    public Fd(TreeItem parent, boolean isSynthetic, String name, String descriptor,
              int access, ObfuscationConfig obfuscationConfig)
    {
      super(parent, isSynthetic, name, descriptor, access, obfuscationConfig);
    }

    /** Return the display name of the descriptor types. */
    protected String getDescriptorName()
    {
        return ";";
    }

    /** Is this field's name a match to the wildcard pattern? */
    public boolean isWildcardMatch(String namePattern) {
        return isMatch(namePattern, getFullInName());
    }

    /** Is this field's name a non-recursive match to the wildcard pattern? */
    public boolean isNRWildcardMatch(String namePattern) {
        return isNRMatch(namePattern, getFullInName());
    }
}

