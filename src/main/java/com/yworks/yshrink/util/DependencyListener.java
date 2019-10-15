package com.yworks.yshrink.util;

import com.yworks.yshrink.util.DependencyEvent;

import java.util.EventListener;

/**
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */

public interface DependencyListener extends EventListener {

  public void dependencyDetected( DependencyEvent de );

}
