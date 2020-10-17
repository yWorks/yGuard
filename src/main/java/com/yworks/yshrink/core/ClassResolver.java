package com.yworks.yshrink.core;

/**
 * The interface Class resolver.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface ClassResolver extends AutoCloseable {

    /**
     * Resolve class.
     *
     * @param className the class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    Class resolve(String className) throws ClassNotFoundException;

}
