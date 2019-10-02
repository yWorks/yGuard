package com.yworks.yshrink.model;

import java.io.File;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class NewNodeDescriptor extends AbstractDescriptor {

  protected NewNodeDescriptor( int access, File sourceJar ) {
    super( access, sourceJar );
  }

  public String toString() {
    return "NewNodeDescriptor{}";
  }
}
