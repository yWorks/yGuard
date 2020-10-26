package com.yworks.common.ant;

import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * The type Attributes section.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class AttributesSection extends PatternMatchedSection {

  private Set<String> attributes = new HashSet<String>( );
  private String attributesStr;

  /**
   * Gets attributes.
   *
   *
		 * @return the attributes
   */
  public Set<String> getAttributes() {
    return attributes;
  }

  /**
   * Gets attributes str.
   *
   *
		 * @return the attributes str
   */
  public String getAttributesStr() {
    return this.attributesStr;
  }

  /**
   * Sets name.
   *
   *
		 * @param attributeStr the attribute str
   */
  public void setName( String attributeStr ) {

    this.attributesStr = attributeStr;
    StringTokenizer tok = new StringTokenizer( attributeStr, "," );
    while ( tok.hasMoreElements() ) {
      attributes.add( tok.nextToken().trim() );
    }

  }

}
