package com.yworks.yshrink.model;

import java.io.File;

/**
 * The type New node descriptor.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class NewNodeDescriptor extends AbstractDescriptor {

  /**
   * Instantiates a new New node descriptor.
   *
   * @param access    the access
   * @param sourceJar the source jar
   */
  protected NewNodeDescriptor( int access, File sourceJar ) {
    super( access, sourceJar );
  }

  public String toString() {
    return "NewNodeDescriptor{}";
  }
}
