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
 * A Tools class containing generally useful, miscellaneous static methods.
 *
 * @author      Mark Welsh
 */
public class Tools
{
    // Constants -------------------------------------------------------------


    // Fields ----------------------------------------------------------------


    // Class Methods ---------------------------------------------------------
    /**
     * Is the string one of the ones in the array?
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
