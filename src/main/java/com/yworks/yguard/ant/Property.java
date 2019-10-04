package com.yworks.yguard.ant;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public final class Property {
  /** Holds value of property name. */
  String name;

  /** Holds value of property value. */
  String value;

  public void setName(String name)
  {
    this.name = name;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
