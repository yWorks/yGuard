package com.yworks.yshrink.ant;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.common.ant.PatternMatchedSection;
import com.yworks.yshrink.util.Util;

import java.util.EnumSet;

/**
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

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = Util.toInternalClass( name );
  }

  public String getExtends() {
    return extendsType;
  }

  public void setExtends( String extendsType ) {
    this.extendsType = Util.toInternalClass( extendsType );
  }

  public String getImplements() {
    return implementsType;
  }

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

  public Access getClassAccess() {
    return classAccess;
  }

  public void setClassAccess( String classAccessStr ) {
    Access acc = accessValue( classAccessStr );

    if ( null != acc ) {
      this.classAccess = acc;
    }
  }

  public Access getMethodAccess() {
    return methodAccess;
  }

  public void setMethodAccess( String methodAccessStr ) {
    Access acc = accessValue( methodAccessStr );

    if ( null != acc ) {
      this.methodAccess = acc;
    }
  }

  public void setClasses( String classAccess ) {
    setClassAccess( classAccess );
  }

  public void setMethods( String methodAccess ) {
    setMethodAccess( methodAccess );
  }

  public Access getFieldAccess() {
    return fieldAccess;
  }

  public void setFieldAccess( String fieldAccessStr ) {

    Access acc = accessValue( fieldAccessStr );

    if ( null != acc ) {
      this.fieldAccess = acc;
    }
  }

  public void setFields( String fieldAccess ) {
    setFieldAccess( fieldAccess );
  }
}
