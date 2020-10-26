/*
 * Filter.java
 *
 * Created on October 24, 2002, 4:51 PM
 */

package com.yworks.util;

/**
 * The interface Filter.
 *
 * @author Sebastian Mueller, yWorks GmbH http://www.yworks.com
 */
public interface Filter
{
    /**
     * Accepts boolean.
     *
     *
		 * @param o the o
     * 
		 * @return the boolean
     */
    boolean accepts(Object o);
}
