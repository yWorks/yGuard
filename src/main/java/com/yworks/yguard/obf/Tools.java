/*
 * YGuard -- an obfuscation library for Java(TM) classfiles.
 *
 * Original Copyright (c) 1999 Mark Welsh (markw@retrologic.com)
 * Modifications Copyright (c) 2002 yWorks GmbH (yguard@yworks.com)
 *
 */
package com.yworks.yguard.obf;

/**
 * A Tools class containing generally useful, miscellaneous static methods.
 *
 * @author Mark Welsh
 */
public class Tools
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------

    /**
     * Is the string one of the ones in the array?
     *
     * @param s    the s
     * @param list the list
     * @return the boolean
     */
    public static boolean isInArray(String s, String[] list)
    {
        for (int i = 0; i < list.length; i++) if (s.equals(list[i])) return true;
        return false;
    }

    /** Encode a byte[] as a Base64 (see RFC1521, Section 5.2) String. */
    private static final char[] base64 = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
        'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final char pad = '=';

    /**
     * To base 64 string.
     *
     * @param b the b
     * @return the string
     */
    public static String toBase64(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        for (int ptr = 0; ptr < b.length; ptr += 3)
        {
            sb.append(base64[(b[ptr] >> 2) & 0x3F]);
            if (ptr + 1 < b.length)
            {
                sb.append(base64[((b[ptr] << 4) & 0x30) | ((b[ptr + 1] >> 4) & 0x0F)]);
                if (ptr + 2 < b.length)
                {
                    sb.append(base64[((b[ptr + 1] << 2) & 0x3C) | ((b[ptr + 2] >> 6) & 0x03)]);
                    sb.append(base64[b[ptr + 2] & 0x3F]);
                }
                else
                {
                    sb.append(base64[(b[ptr + 1] << 2) & 0x3C]);
                    sb.append(pad);
                }
            }
            else
            {
                sb.append(base64[((b[ptr] << 4) & 0x30)]);
                sb.append(pad);
                sb.append(pad);
            }
        }
        return sb.toString();
    }
}
