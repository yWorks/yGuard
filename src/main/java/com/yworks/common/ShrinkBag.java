package com.yworks.common;

import java.io.File;

/**
 * The interface Shrink bag.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface ShrinkBag {

    /**
     * Sets in.
     *
     *
		 * @param file the file
     */
    void setIn( File file );

    /**
     * Sets out.
     *
     *
		 * @param file the file
     */
    void setOut( File file );

    /**
     * Gets in.
     *
     *
		 * @return the in
     */
    File getIn();

    /**
     * Gets out.
     *
     *
		 * @return the out
     */
    File getOut();

    /**
     * Is entry point jar boolean.
     *
     *
		 * @return the boolean
     */
    boolean isEntryPointJar();

    /**
     * Sets resources.
     *
     *
		 * @param resourcesStr the resources str
     */
    void setResources( String resourcesStr );

    /**
     * Gets resources.
     *
     *
		 * @return the resources
     */
    ResourcePolicy getResources();
}
