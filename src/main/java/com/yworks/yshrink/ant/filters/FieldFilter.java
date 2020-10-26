package com.yworks.yshrink.ant.filters;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.yshrink.ant.FieldSection;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.util.Util;
import org.apache.tools.ant.Project;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Field filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class FieldFilter extends PatternMatchedFilter {

  private List<FieldSection> sections;

    /**
     * Instantiates a new Field filter.
     *
     * @param project the project
     */
    public FieldFilter( final Project project ) {
    super( project );
  }

    /**
     * Add field section.
     *
     * @param fieldSection the field section
     */
    public void addFieldSection( FieldSection fieldSection ) {
    if ( null == sections ) {
      sections = new ArrayList<FieldSection>( 5 );
    }
    sections.add( fieldSection );
  }

  @Override
  public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd ) {

    String className = cd.getName();
    String fieldName = fd.getName();

    for ( FieldSection fs : sections ) {

      boolean r = true;

      String entryFieldClass = fs.getClassName();
      String entryFieldName = fs.getName();

      // type
      if ( null != fs.getType() ) {
        Type requiredType = Type.getType( Util.verboseToNativeType( fs.getType() ) );
        r &= ( requiredType.equals( fd.getDesc() ) );
      }

      // access
      if ( null != fs.getAccess() ) {
        r &= fs.getAccess().isAccessLevel( fd.getAccess() );
      }

      // class
      if ( null == entryFieldClass || entryFieldClass.length() == 0 ) {
        r &= match( TypePatternSet.Type.CLASS, className, fs ) ||
                match( TypePatternSet.Type.CLASS, Util.toJavaClass( className ), fs );
      } else {
        r &= entryFieldClass.equals( className );
      }

      // name
      if ( null == entryFieldName || entryFieldName.length() == 0 ) {
        r &= match( TypePatternSet.Type.NAME, fieldName, fs );
      } else {
        r &= entryFieldName.equals( fieldName );
      }

      if ( r ) {
        return r;
      }
    }

    return false;
  }
}
