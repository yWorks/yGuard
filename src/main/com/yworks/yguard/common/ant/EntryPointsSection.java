package com.yworks.yguard.common.ant;

import com.yworks.yshrink.ant.ClassSection;
import com.yworks.yshrink.ant.FieldSection;
import com.yworks.yshrink.ant.MethodSection;

import java.util.ArrayList;
import java.util.List;

/**
 * ANT entryPoint section
 */
public class EntryPointsSection extends Exclude {

  private List<MethodSection> methodSections = new ArrayList<MethodSection>( 5 );
  private List<ClassSection> classSections = new ArrayList<ClassSection>( 5 );
  private List<FieldSection> fieldSections = new ArrayList<FieldSection>( 5 );
  private List<AttributesSection> attributesSections = new ArrayList<AttributesSection>( 1 );

  public EntryPointsSection( YGuardBaseTask task ) {
    super( task );
  }

  public void addConfiguredMethod( MethodSection ms ) {
    this.methodSections.add( ms );
  }

  public void addConfiguredClass( ClassSection cs ) {
    this.classSections.add( cs );
  }

  public void addConfiguredField( FieldSection fs ) {
    this.fieldSections.add( fs );
  }

  public void addConfiguredAttribute( AttributesSection as ) {
    this.attributesSections.add( as );
  }

  public List<MethodSection> getMethodSections() {
    return methodSections;
  }

  public List<ClassSection> getClassSections() {
    return classSections;
  }

  public List<FieldSection> getFieldSections() {
    return fieldSections;
  }

  public List<AttributesSection> getAttributesSections() {
    return attributesSections;
  }


}
