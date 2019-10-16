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

import java.io.*;
import java.util.*;

/**
 * Name generator that uses (almost) the full Java identifier namespace,
 * and chooses to put some of the keyword names (legal in JVM, illegal in
 * Java language) out front in sequence.
 *
 * @author      Mark Welsh
 */
public class KeywordNameMaker implements NameMaker
{
    // Constants -------------------------------------------------------------
    private static final String DUMMY_ARG_LIST = "dummy";


    // Fields ----------------------------------------------------------------
    private int skipped = 0; // Names skipped in the sequence
    private Vector namesToDate = new Vector();
    private Hashtable argCount = new Hashtable();
    private String[] noObfNames = null; // List of names not to be obfuscated
    private String[] keywordsToUse;
    private String[] keywordsToExclude;
    private String[] firstLetter;
    private String[] nextLetter;
    private String[] noKeywords = {};
    private String[] someKeywords = {
        "a", "if", "do", "for", "int", "new", "try", "byte", "case", "char", 
        "else", "goto", "long", "null", "void"
    };
    private String[] allKeywords = {
        "if", "do", "for", "int", "new", "try", "byte", "case", "char", 
        "else", "goto", "long", "null", "this", "void", "true", "false", 
        "break", "catch", "class", "const", "float", "final", "short", 
        "super", "throw", "while", "double", "import", "native", "public", 
        "return", "static", "switch", "throws", "boolean", "default", 
        "extends", "finally", "package", "private", "abstract", "continue", 
        "volatile", "interface", "protected", "transient", "implements", 
        "instanceof", "synchronized"
    };
    private String[] firstLetterLower = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
                                    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                                    "y", "z"};
    private String[] nextLetterLower  = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
                                    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                                    "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private String[] firstLetterAll = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
                                    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                                    "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                                    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                                    "W", "X", "Y", "Z"};
    private String[] nextLetterAll  = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
                                    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                                    "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                                    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                                    "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};


    // Class Methods ---------------------------------------------------------
    /** Main method for testing. */
/*    public static void main(String[] args) 
    {
        PrintWriter pw = new PrintWriter(
                         new BufferedOutputStream(
                         new FileOutputStream("keywordnamemaker.tst")));
        try
        {
            NameMaker nmk = new KeywordNameMaker();
            for (int i = 0; i < 1000000; i++)
            {
                pw.println(nmk.nextName(null));
            }
        }
        finally
        {
            pw.close();
        }
    }
*/

    // Instance Methods ------------------------------------------------------
    /** Ctor. */
    public KeywordNameMaker()
    {
        this(null);
    }

    /** Ctor - block names not to be obfuscated from the mapping target space. */
    public KeywordNameMaker(String[] noObfNames)
    {
        this(noObfNames, true);
    }

    /** Ctor - block names not to be obfuscated from the mapping target space. */
    public KeywordNameMaker(String[] noObfNames, boolean useKeywords)
    {
        this(noObfNames, true, false);
    }

    /** Ctor - block names not to be obfuscated from the mapping target space. */
    public KeywordNameMaker(String[] noObfNames, boolean useKeywords, boolean lowerCaseOnly)
    {
        this.noObfNames = noObfNames == null ? new String[0] : noObfNames;
        if (useKeywords) 
        {
            keywordsToUse = someKeywords;
            keywordsToExclude = someKeywords;
        }
        else
        {
            keywordsToUse = noKeywords;
            keywordsToExclude = allKeywords;
        }
        if (lowerCaseOnly) 
        {
            firstLetter = firstLetterLower;
            nextLetter = nextLetterLower;
        } 
        else 
        {
            firstLetter = firstLetterAll;
            nextLetter = nextLetterAll;
        }
    }

    /** Return the next unique name for this namespace. */
    public String nextName(String descriptor) 
    {
        // Check for arg-list in hashtable
        String argList = DUMMY_ARG_LIST;
        if (descriptor != null) 
        {
            argList = getArgList(descriptor);
        }
        Integer intCount = (Integer)argCount.get(argList);
        int theCount = 0;
        if (intCount == null)
        {
            argCount.put(argList, new Integer(theCount));
        }
        else
        {
            theCount = intCount.intValue() + 1;
            argCount.remove(argList);
            argCount.put(argList, new Integer(theCount));
        }
        return getName(theCount);
    }

    // Extract the arg-list from a descriptor
    private String getArgList(String descriptor) 
    {
        int pos = descriptor.indexOf(')');
        return descriptor.substring(1, pos);
    }

    // Generate i'th allowed, unique name
    private String getName(int index) 
    {
        // If we have previously computed this name, just return it
        String name = null;
        if (index < namesToDate.size())
        {
            name = (String)namesToDate.elementAt(index);
        }
        else
        {
            // Generate a new valid name for the sequence
            for (;;)
            {
                name = getNewName(index + skipped);
                if (!Tools.isInArray(name, noObfNames) &&
                    (index + skipped < keywordsToUse.length || 
                     !Tools.isInArray(name, keywordsToExclude)))
                {
                    break;
                }
                skipped++;
            }
            namesToDate.addElement(name);
        }
        return name;
    }

    // Generate j'th name in sequence (can repeat keywords)
    private String getNewName(int index) 
    {
        String name = null;

        // Check if we are in the 'keyword' part of the namespace
        if (index < keywordsToUse.length)
        {
            name = keywordsToUse[index];
        }
        else
        {
            // Check if we are in the single letter part of the namespace
            index -= keywordsToUse.length;
            if (index < firstLetter.length)
            {
                name = firstLetter[index];
            }
            else
            {
                // We are in the >=2 letter part of namespace
                index -= firstLetter.length;
                int nextLetters = 1;
                int subspaceSize = nextLetter.length;
                while (index >= firstLetter.length * subspaceSize)
                {
                    index -= firstLetter.length * subspaceSize;
                    nextLetters++;
                    subspaceSize *= nextLetter.length;
                }

                // Pull out the name
                StringBuffer sb = new StringBuffer(firstLetter[index / subspaceSize]);
                while (subspaceSize != 1)
                {
                    index %= subspaceSize;
                    subspaceSize /= nextLetter.length;
                    sb.append(nextLetter[index / subspaceSize]);
                }

                // Check for collision with keywords
                name = sb.toString();
            }
        }
        return name;
    }
}

