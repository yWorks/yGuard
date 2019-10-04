package com.yworks.yshrink.ant;

import com.yworks.yguard.common.ant.PatternMatchedSection;

import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class AttributesSection extends PatternMatchedSection {

  private Set<String> attributes = new HashSet<String>( );

  public Set<String> getAttributes() {
    return attributes;
  }

  public void setName( String attributeStr ) {

    StringTokenizer tok = new StringTokenizer( attributeStr, "," );
    while ( tok.hasMoreElements() ) {
      attributes.add( tok.nextToken().trim() );
    }

  }

}
