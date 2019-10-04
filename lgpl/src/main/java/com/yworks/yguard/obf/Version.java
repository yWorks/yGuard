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


/**
 * Central point for version and build control.
 *
 * @author Mark Welsh, yWorks' yGuard Team
 */
public class Version {
    // Constants -------------------------------------------------------------
    private static final String REL_VERSION = "@VERSION@";
    private static final String YGUARD_REL_JAR_COMMENT = null; // "Obfuscation by yGuard v"+REL_VERSION+" - www.yworks.com (authors: Mark Welsh, markw@retrologic.com; yWorks' yGuard Development Team, yguard@yworks.com)";


    // Class Methods ---------------------------------------------------------
    /** Return the current major.minor version string. */
    public static String getVersion() {
        return REL_VERSION;
    }

    /** Return the current Jar comment String. */
    public static String getJarComment() {
        return YGUARD_REL_JAR_COMMENT;
    }
}
