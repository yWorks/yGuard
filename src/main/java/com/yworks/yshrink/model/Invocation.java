package com.yworks.yshrink.model;

/**
 * The type Invocation.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Invocation {

  private final int opcode;
  private final String type;
  private final String name;
  private final String desc;

  /**
   * Instantiates a new Invocation.
   *
   * @param opcode the opcode
   * @param type   the type
   * @param name   the name
   * @param desc   the desc
   */
  public Invocation( int opcode, String type, String name, String desc ) {
    this.opcode = opcode;
    this.type = type;
    this.name = name;
    this.desc = desc;
  }

  /**
   * Gets opcode.
   *
   * @return the opcode
   */
  public int getOpcode() {
    return opcode;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() {
    return type;
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
   * Gets desc.
   *
   * @return the desc
   */
  public String getDesc() {
    return desc;
  }
}
