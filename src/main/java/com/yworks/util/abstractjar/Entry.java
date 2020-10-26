package com.yworks.util.abstractjar;

/**
 * Entry represents an entry in an archive, e.g class file or resource file
 */
public interface Entry {
    /**
     * Is directory boolean.
     *
     *
		 * @return the boolean
     */
    boolean isDirectory();

    /**
     * Gets name.
     *
     *
		 * @return the name
     */
    String getName();

    /**
     * Gets size.
     *
     *
		 * @return the size
     */
    long getSize();
}
