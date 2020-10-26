package com.yworks.yguard.ant;

/**
 * The type Property.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public final class Property {
  /**
   * Holds value of property name.
   */
  String name;

  /**
   * Holds value of property value.
   */
  String value;

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Sets value.
   *
   * @param value the value
   */
  public void setValue(String value)
  {
    this.value = value;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }
}
