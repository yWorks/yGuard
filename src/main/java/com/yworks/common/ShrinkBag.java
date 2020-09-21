package com.yworks.common;

import java.io.File;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface ShrinkBag {

  void setIn( File file );

  void setOut( File file );

  File getIn();

  File getOut();

  boolean isEntryPointJar();

  void setResources( String resourcesStr );

  ResourcePolicy getResources();
}
