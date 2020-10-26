/**
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *

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

    /**
     * Return the current major.minor version string.  
		 * @return the version
     */
    public static String getVersion() {
        return REL_VERSION;
    }

    /**
     * Return the current Jar comment String.  
		 * @return the jar comment
     */
    public static String getJarComment() {
        return YGUARD_REL_JAR_COMMENT;
    }
}
