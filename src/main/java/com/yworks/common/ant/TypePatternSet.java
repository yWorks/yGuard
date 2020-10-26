package com.yworks.common.ant;

import org.apache.tools.ant.types.PatternSet;

/**
 * The type Type pattern set.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class TypePatternSet extends PatternSet {

    /**
     * The enum Type.
     */
    public enum Type {
        /**
         * Class type.
         */
        CLASS,
        /**
         * Name type.
         */
        NAME,
        /**
         * Package type.
         */
        PACKAGE,
        /**
         * Extends type.
         */
        EXTENDS,
        /**
         * Implements type.
         */
        IMPLEMENTS
  }

  private Type type = Type.NAME;

    /**
     * Gets type.
     *
     *
		 * @return the type
     */
    public Type getType() {
    return type;
  }

    /**
     * Sets type.
     *
     *
		 * @param type the type
     */
    public void setType( String type ) {
    this.type = Type.valueOf( type.toUpperCase() );
  }
}
