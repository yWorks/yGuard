package com.yworks.yshrink.model;

import java.io.File;

/**
 * The type Field descriptor.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class FieldDescriptor extends AbstractDescriptor {

  private String desc;
  private String name;

    /**
     * Instantiates a new Field descriptor.
     *
     * 
		 * @param desc      the desc
     * 
		 * @param name      the name
     * 
		 * @param access    the access
     * 
		 * @param sourceJar the source jar
     */
    protected FieldDescriptor( final String desc, final String name, final int access, File sourceJar ) {
    super( access, sourceJar );
    this.desc = desc;
    this.name = name;
  }

    /**
     * Gets desc.
     *
     * 
		 * @return the desc
     */
    public String getDesc() {
    return desc;
  }

    /**
     * Gets name.
     *
     * 
		 * @return the name
     */
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
