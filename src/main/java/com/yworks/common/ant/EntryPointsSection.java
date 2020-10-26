package com.yworks.common.ant;

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

  /**
   * Instantiates a new Entry points section.
   *
   * @param task the task
   */
  public EntryPointsSection( YGuardBaseTask task ) {
    super( task );
  }

  /**
   * Add configured method.
   *
   * @param ms the ms
   */
  public void addConfiguredMethod( MethodSection ms ) {
    this.methodSections.add( ms );
  }

  /**
   * Add configured class.
   *
   * @param cs the cs
   */
  public void addConfiguredClass( ClassSection cs ) {
    this.classSections.add( cs );
  }

  /**
   * Add configured field.
   *
   * @param fs the fs
   */
  public void addConfiguredField( FieldSection fs ) {
    this.fieldSections.add( fs );
  }

  /**
   * Add configured attribute.
   *
   * @param as the as
   */
  public void addConfiguredAttribute( AttributesSection as ) {
    this.attributesSections.add( as );
  }

  /**
   * Gets method sections.
   *
   * @return the method sections
   */
  public List<MethodSection> getMethodSections() {
    return methodSections;
  }

  /**
   * Gets class sections.
   *
   * @return the class sections
   */
  public List<ClassSection> getClassSections() {
    return classSections;
  }

  /**
   * Gets field sections.
   *
   * @return the field sections
   */
  public List<FieldSection> getFieldSections() {
    return fieldSections;
  }

  /**
   * Gets attributes sections.
   *
   * @return the attributes sections
   */
  public List<AttributesSection> getAttributesSections() {
    return attributesSections;
  }


}
