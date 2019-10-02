package com.yworks.yshrink.model;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Invocation {

  private int opcode;
  private String type;
  private String name;
  private String desc;

  public Invocation( int opcode, String type, String name, String desc ) {
    this.opcode = opcode;
    this.type = type;
    this.name = name;
    this.desc = desc;
  }

  public int getOpcode() {
    return opcode;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }
}
