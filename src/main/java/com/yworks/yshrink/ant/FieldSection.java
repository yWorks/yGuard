package com.yworks.yshrink.ant;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.PatternMatchedSection;
import com.yworks.yshrink.util.Util;

import java.util.EnumSet;

/**
 * The type Field section.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class FieldSection extends PatternMatchedSection {

  private String name;
  private String className;
  private String type;

  {
    types = EnumSet.of(
        TypePatternSet.Type.NAME,
        TypePatternSet.Type.CLASS
    );
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
   * Sets name.
   *
   * @param name the name
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets class name.
   *
   * @return the class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Sets class.
   *
   * @param className the class name
   */
  public void setClass( String className ) {
    this.className = Util.toInternalClass( className );
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
   * Sets type.
   *
   * @param type the type
   */
  public void setType( String type ) {
    this.type = Util.toInternalClass( type );
  }

  @Override
  public TypePatternSet createPatternSet() {
    TypePatternSet typePatternSet = newTypePatternSet();
    typePatternSet.setType( "class" );
    addPatternSet( typePatternSet, typePatternSet.getType() );
    return typePatternSet;
  }

  /**
   * Instantiates a type pattern set,
   * subclasses may provide custom implementations.
   *
   * @return the new type pattern set
   */
  protected TypePatternSet newTypePatternSet() {
    return new TypePatternSet();
  }
}
