package com.yworks.common.ant;

import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class AttributesSection extends PatternMatchedSection {

  private Set<String> attributes = new HashSet<String>( );
  private String attributesStr;

  public Set<String> getAttributes() {
    return attributes;
  }

  public String getAttributesStr() {
    return this.attributesStr;
  }

  public void setName( String attributeStr ) {

    this.attributesStr = attributeStr;
    StringTokenizer tok = new StringTokenizer( attributeStr, "," );
    while ( tok.hasMoreElements() ) {
      attributes.add( tok.nextToken().trim() );
    }

  }

}
