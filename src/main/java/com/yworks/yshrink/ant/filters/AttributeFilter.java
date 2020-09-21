package com.yworks.yshrink.ant.filters;

import com.yworks.common.ant.AttributesSection;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.util.Util;
import com.yworks.common.ant.TypePatternSet;

import java.util.List;
import java.util.ArrayList;

import org.apache.tools.ant.Project;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class AttributeFilter extends PatternMatchedFilter {

  private List<AttributesSection> sections = new ArrayList<AttributesSection>( );

  public AttributeFilter( Project p ) {
    super( p );
  }

  public void addAttributesSection( AttributesSection as ) {
    sections.add( as );
  }

  public void setRetainAttribute( ClassDescriptor cd ) {

    String className = cd.getName();
    String javaClassName = Util.toJavaClass( cd.getName() );

    for ( AttributesSection section : sections ) {
      if ( match( TypePatternSet.Type.NAME, javaClassName, section ) ||
            match( TypePatternSet.Type.NAME, className, section ) ) {

        for ( String attr : section.getAttributes() ) {
          cd.setRetainAttribute( attr );
        }
      }
    }
  }


}
