package com.yworks.yshrink.ant.filters;

import com.yworks.common.ant.PatternMatchedSection;
import com.yworks.common.ant.TypePatternSet;
import com.yworks.yshrink.ant.ClassSection;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.util.Util;
import org.apache.tools.ant.Project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The type Class filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class ClassFilter extends PatternMatchedFilter {

  private List<ClassSection> sections;

    /**
     * Instantiates a new Class filter.
     *
     * @param project the project
     */
    public ClassFilter( Project project ) {
    super( project );
  }

  @Override
  public boolean isEntryPointClass( final Model model, final ClassDescriptor cd ) {
    boolean r = false;
    for ( ClassSection cs : sections ) {
      if (matches(cs, model, cd)) {
        return true;
      }
    }
    return false;
  }

  private boolean matches( final ClassSection cs, final Model model, final ClassDescriptor cd ) {

    String className = cd.getName();

    boolean r = true;

    // name, access
//    r &= isEntryPointClass( model, cd );

    // access
    if ( null != cs.getClassAccess() && ( cs.getName() == null || cs.getName() == "" ) && ( !cs.getClassAccess().equals( PatternMatchedSection.Access.NONE ) ) )
    {
      r &= cs.getClassAccess().isAccessLevel( cd.getAccess() );
    }

    // name
    String entryClassName = cs.getName();
    if ( null == entryClassName || entryClassName.length() == 0 ) {
      r &= ( match( TypePatternSet.Type.NAME, Util.toJavaClass( className ), cs )
              ||
              match( TypePatternSet.Type.NAME, className, cs ) );
    } else {
      r &= entryClassName.equals( className );
    }

    // extends
    if ( null != cs.getExtends() ) {
      boolean self = cs.getExtends().equals( cd.getName() );
      if ( !self ) {
        Collection<String> ancestors = cd.getAllAncestorClasses( model );
        r &= ancestors.contains( cs.getExtends() );
      } else {
        r &= self;
      }
    }

    // implements
    if ( null != cs.getImplements() ) {
      boolean self = cs.getImplements().equals( cd.getName() );
      if ( !self ) {
        Collection<String> interfaces = cd.getAllImplementedInterfaces( model );
        r &= interfaces.contains( cs.getImplements() );
      } else {
        r &= self;
      }
    }

    return r;
  }

  private List<ClassSection> getAllMatchingClassSections( final Model model, final ClassDescriptor cd ) {

    List<ClassSection> matchingSections = new ArrayList<ClassSection>();

    for ( ClassSection cs : sections ) {
      if ( matches( cs, model, cd ) ) {
        matchingSections.add( cs );
      }
    }

    return matchingSections;
  }

  @Override
  public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd ) {

    for ( ClassSection cs : getAllMatchingClassSections( model, cd ) ) {

      boolean r = false;

      PatternMatchedSection.Access acc = cs.getFieldAccess();
      if ( null != acc ) {
        r = acc.isAccessLevel( fd.getAccess() );
      }

      if ( r ) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {

    for ( ClassSection cs : getAllMatchingClassSections( model, cd ) ) {

      boolean r = true;

      PatternMatchedSection.Access acc = cs.getMethodAccess();
      if ( null != acc ) {
        r = r && acc.isAccessLevel( md.getAccess() );
      }

      if ( r ) {
        return true;
      }
    }

    return false;
  }

    /**
     * Add class section.
     *
     * @param cs the cs
     */
    public void addClassSection( ClassSection cs ) {
    if ( null == sections ) {
      sections = new ArrayList<ClassSection>( 5 );
    }
    sections.add( cs );
  }
}
