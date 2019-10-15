package com.yworks.yguard.common.ant;

import org.apache.tools.ant.types.PatternSet;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class TypePatternSet extends PatternSet {

  public enum Type {
    CLASS,NAME,PACKAGE,EXTENDS,IMPLEMENTS
  }

  private Type type = Type.NAME;

  public Type getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = Type.valueOf( type.toUpperCase() );
  }
}
