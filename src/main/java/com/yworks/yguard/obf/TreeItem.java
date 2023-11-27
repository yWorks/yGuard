/* ===========================================================================
 * $RCSfile$
 * ===========================================================================
 *
 * RetroGuard -- an obfuscation package for Java classfiles.
 *
 * Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 *

 *
 *
 * $Date$
 * $Revision$
 */
package com.yworks.yguard.obf;

import java.util.*;
import com.yworks.yguard.obf.classfile.*;

/**
 * Item that forms a tree structure and can represent a package level, a class,
 * or a method or field.
 *
 * @author Mark Welsh
 */
public class TreeItem {
    // Constants -------------------------------------------------------------

    // Fields ----------------------------------------------------------------
    /**
     * The Is synthetic.
     */
    protected boolean isSynthetic; // Is a method or field Synthetic?
    /**
     * The Access.
     */
    protected int access; // Access level (interpret using java.lang.reflect.Modifier)
    /**
     * The Class tree.
     */
    protected ClassTree classTree = null; // Our owner
    /**
     * The Parent.
     */
    protected TreeItem parent = null; // Our immediate parent
    /**
     * The Sep.
     */
    protected String sep = ClassFile.SEP_REGULAR; // Separator preceeding this level's name
    private String inName = null; // Original name of this item
    private String outName = null; // Output name of this item
    private boolean isFixed = false; // Has the name been fixed in some way?
    private boolean isFromScript = false; // Is this script constrained?
    private boolean isFromScriptMap = false; // Is this script_map constrained?

    // Class Methods ---------------------------------------------------------
    /**
     * Checks if two strings match using a non-package-recursive wildcard string
     * pattern.
     *
     * @param wildcardPattern The wildcard pattern to match against.
     * @param inputString     The input string to compare.
     * @return True if the input string matches the wildcard pattern, false
     *         otherwise.
     */
    public static boolean isNonRecursiveWildcardMatch(String wildcardPattern, String inputString) {
        Enumeration patternEnum, stringEnum;
        try {
            for (patternEnum = ClassTree.getNameEnum(wildcardPattern), stringEnum = ClassTree
                    .getNameEnum(inputString); patternEnum.hasMoreElements() && stringEnum.hasMoreElements();) {

                Cons patternSegment = (Cons) patternEnum.nextElement();
                char patternTag = ((Character) patternSegment.car).charValue();
                String patternName = (String) patternSegment.cdr;

                Cons stringSegment = (Cons) stringEnum.nextElement();
                char stringTag = ((Character) stringSegment.car).charValue();
                String stringName = (String) stringSegment.cdr;

                if (patternTag != stringTag || !isMatch(patternName, stringName)) {
                    return false;
                }
            }
            if (patternEnum.hasMoreElements() || stringEnum.hasMoreElements()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Do a wildcard String match.
     *
     * @param pattern the pattern
     * @param string  the string
     * @return the boolean
     */
    public static boolean isMatch(String pattern, String string) {
        // Sanity check
        if (pattern == null || string == null) {
            return false;
        }

        // System.out.println("1) Pattern: " + pattern + " String: " + string);

        // Not really a wildcard, then check for exact match
        if (pattern.indexOf('*') == -1) {
            return pattern.equals(string);
        }

        // Check for match of head
        int pos = -1;
        if (pattern.charAt(0) != '*') {
            pos = pattern.indexOf('*');
            String head = pattern.substring(0, pos);
            if (string.length() < head.length()) {
                return false;
            }
            if (!string.substring(0, head.length()).equals(head)) {
                return false;
            } else {
                pattern = pattern.substring(pos);
                string = string.substring(pos);
            }
        }

        // System.out.println("2) Pattern: " + pattern + " String: " + string);

        // Check for match of tail
        if (pattern.charAt(pattern.length() - 1) != '*') {
            pos = pattern.lastIndexOf('*');
            String tail = pattern.substring(pos + 1);
            if (string.length() < tail.length()) {
                return false;
            }
            if (!string.substring(string.length() - tail.length()).equals(tail)) {
                return false;
            } else {
                pattern = pattern.substring(0, pos + 1);
                string = string.substring(0, string.length() - tail.length());
            }
        }

        // System.out.println("3) Pattern: " + pattern + " String: " + string);

        // Split the pattern at the wildcard positions
        Vector section = new Vector();
        pos = pattern.indexOf('*');
        int rpos = -1;
        while ((rpos = pattern.indexOf('*', pos + 1)) != -1) {
            if (rpos != pos + 1) {
                section.addElement(pattern.substring(pos + 1, rpos));
            }
            pos = rpos;
        }
        // Check each section for a non-overlapping match in the string
        for (Enumeration enumeration = section.elements(); enumeration.hasMoreElements();) {
            String chunk = (String) enumeration.nextElement();
            // System.out.println("Section: " + chunk + " String: " + string);
            pos = string.indexOf(chunk);
            if (pos == -1) {
                return false;
            }
            string = string.substring(pos + chunk.length());
        }
        return true;
    }

    // Instance Methods ------------------------------------------------------

    /**
     * Ctor.
     *
     * @param parent the parent
     * @param name   the name
     */
    public TreeItem(TreeItem parent, String name) {
        this.parent = parent;
        this.inName = name;
        if (parent != null) {
            classTree = parent.classTree;
        }
    }

    /**
     * Return the modifiers.
     *
     * @return the modifiers
     */
    public int getModifiers() {
        return access;
    }

    /**
     * Return the original name of the entry.
     *
     * @return the in name
     */
    public String getInName() {
        return inName;
    }

    /**
     * Set the output name of the entry.
     *
     * @param outName the out name
     */
    public void setOutName(String outName) {
        // DEBUG
        // if (isFixed)
        // {
        // System.out.println("BIG TROUBLE: " + inName + " -> " + this.outName + " -> "
        // + outName);
        // }
        this.outName = outName;
        isFixed = true;
    }

    /**
     * Return the output name of the entry, obfuscated or original.
     *
     * @return the out name
     */
    public String getOutName() {
        return outName != null ? outName : inName;
    }

    /**
     * Return the obfuscated name of the entry.
     *
     * @return the obf name
     */
    public String getObfName() {
        return outName;
    }

    /**
     * Signal that this constraint came from a user script line.
     */
    public void setFromScript() {
        isFromScript = true;
    }

    /**
     * Signal that this constraint came from a map script line.
     */
    public void setFromScriptMap() {
        isFromScriptMap = true;
    }

    /**
     * Has the entry been fixed already?
     *
     * @return the boolean
     */
    public boolean isFixed() {
        return isFixed;
    }

    /**
     * Is this constrained by a user script line?
     *
     * @return the boolean
     */
    public boolean isFromScript() {
        return isFromScript;
    }

    /**
     * Is this constrained by a map script line?
     *
     * @return the boolean
     */
    public boolean isFromScriptMap() {
        return isFromScriptMap;
    }

    /**
     * Is a method or field Synthetic?
     *
     * @return the boolean
     */
    public boolean isSynthetic() {
        return isSynthetic;
    }

    /**
     * Set the parent in the tree -- used when stitching in a Cl to replace a
     * PlaceholderCl.
     *
     * @param parent the parent
     */
    public void setParent(TreeItem parent) {
        this.parent = parent;
    }

    /**
     * Get the parent in the tree.
     *
     * @return the parent
     */
    public TreeItem getParent() {
        return parent;
    }

    /**
     * Construct and return the full original name of the entry.
     *
     * @return the full in name
     */
    public String getFullInName() {
        if (parent == null) {
            return "";
        } else if (parent.parent == null) {
            return getInName();
        } else {
            return parent.getFullInName() + sep + getInName();
        }
    }

    /**
     * Construct and return the full obfuscated name of the entry.
     *
     * @return the full out name
     */
    public String getFullOutName() {
        if (parent == null) {
            return "";
        } else if (parent.parent == null) {
            return getOutName();
        } else {
            return parent.getFullOutName() + sep + getOutName();
        }
    }
}
