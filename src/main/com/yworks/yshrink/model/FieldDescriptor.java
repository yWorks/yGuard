package com.yworks.yshrink.model;

import java.io.File;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class FieldDescriptor extends AbstractDescriptor {

  private String desc;
  private String name;

  protected FieldDescriptor( final String desc, final String name, final int access, File sourceJar ) {
    super( access, sourceJar );
    this.desc = desc;
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return "FieldDescriptor{" +
        "name='" + name + '\'' +
        ", type='" + desc + '\'' +
        '}';
  }
}
