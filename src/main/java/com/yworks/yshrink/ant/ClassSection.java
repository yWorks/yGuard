package com.yworks.yshrink.ant;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.PatternMatchedSection;
import com.yworks.yshrink.util.Util;

import java.util.EnumSet;

/**
 * The type Class section.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public final class ClassSection extends PatternMatchedSection {

  private String name;
  private String extendsType;
  private String implementsType;
  private Access classAccess = Access.NONE;
  private Access methodAccess = Access.NONE;
  private Access fieldAccess = Access.NONE;

  {
    types = EnumSet.of(
        TypePatternSet.Type.NAME // , EXTENDS

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
    this.name = Util.toInternalClass( name );
  }

    /**
     * Gets extends.
     *
     * @return the extends
     */
    public String getExtends() {
    return extendsType;
  }

    /**
     * Sets extends.
     *
     * @param extendsType the extends type
     */
    public void setExtends( String extendsType ) {
    this.extendsType = Util.toInternalClass( extendsType );
  }

    /**
     * Gets implements.
     *
     * @return the implements
     */
    public String getImplements() {
    return implementsType;
  }

    /**
     * Sets implements.
     *
     * @param implementsType the implements type
     */
    public void setImplements( String implementsType ) {
    this.implementsType = Util.toInternalClass( implementsType );
  }

  @Override
  public void setAccess( String access ) {
    super.setAccess( access );
    setClassAccess( access );
    setMethodAccess( access );
    setFieldAccess( access );
  }

    /**
     * Gets class access.
     *
     * @return the class access
     */
    public Access getClassAccess() {
    return classAccess;
  }

    /**
     * Sets class access.
     *
     * @param classAccessStr the class access str
     */
    public void setClassAccess( String classAccessStr ) {
    Access acc = accessValue( classAccessStr );

    if ( null != acc ) {
      this.classAccess = acc;
    }
  }

    /**
     * Gets method access.
     *
     * @return the method access
     */
    public Access getMethodAccess() {
    return methodAccess;
  }

    /**
     * Sets method access.
     *
     * @param methodAccessStr the method access str
     */
    public void setMethodAccess( String methodAccessStr ) {
    Access acc = accessValue( methodAccessStr );

    if ( null != acc ) {
      this.methodAccess = acc;
    }
  }

    /**
     * Sets classes.
     *
     * @param classAccess the class access
     */
    public void setClasses( String classAccess ) {
    setClassAccess( classAccess );
  }

    /**
     * Sets methods.
     *
     * @param methodAccess the method access
     */
    public void setMethods( String methodAccess ) {
    setMethodAccess( methodAccess );
  }

    /**
     * Gets field access.
     *
     * @return the field access
     */
    public Access getFieldAccess() {
    return fieldAccess;
  }

    /**
     * Sets field access.
     *
     * @param fieldAccessStr the field access str
     */
    public void setFieldAccess( String fieldAccessStr ) {

    Access acc = accessValue( fieldAccessStr );

    if ( null != acc ) {
      this.fieldAccess = acc;
    }
  }

    /**
     * Sets fields.
     *
     * @param fieldAccess the field access
     */
    public void setFields( String fieldAccess ) {
    setFieldAccess( fieldAccess );
  }
}
