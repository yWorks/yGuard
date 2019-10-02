package com.yworks.yshrink.core;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface ClassResolver extends AutoCloseable {

  Class resolve(String className) throws ClassNotFoundException;

}
