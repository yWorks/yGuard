package com.yworks.yshrink.util;

import java.util.EventObject;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */

public class DependencyEvent extends EventObject {

  private String srcClass;
  private String srcMethod;
  private String dstClass;
  private String dstMethod;

  public DependencyEvent( final Object source, final String srcClass, final String srcMethod, final String dstClass,
                          final String dstMethod ) {

    super( source );

    this.srcClass = srcClass;
    this.srcMethod = srcMethod;
    this.dstClass = dstClass;
    this.dstMethod = dstMethod;
  }

  public String getSrcClass() {
    return srcClass;
  }

  public String getSrcMethod() {
    return srcMethod;
  }

  public String getDstClass() {
    return dstClass;
  }

  public String getDstMethod() {
    return dstMethod;
  }
}
