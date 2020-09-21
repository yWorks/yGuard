package com.yworks.yshrink.ant;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.PatternMatchedSection;
import com.yworks.yshrink.util.Util;

import java.util.EnumSet;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public final class FieldSection extends PatternMatchedSection {

  private String name;
  private String className;
  private String type;

  {
    types = EnumSet.of(
        TypePatternSet.Type.NAME,
        TypePatternSet.Type.CLASS
    );
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getClassName() {
    return className;
  }

  public void setClass( String className ) {
    this.className = Util.toInternalClass( className );
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = Util.toInternalClass( type );
  }

  @Override
  public TypePatternSet createPatternSet() {
    TypePatternSet typePatternSet = new TypePatternSet();
    typePatternSet.setType( "class" );
    addPatternSet( typePatternSet, typePatternSet.getType() );
    return typePatternSet;
  }
}
