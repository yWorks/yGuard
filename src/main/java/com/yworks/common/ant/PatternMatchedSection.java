package com.yworks.common.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.PatternSet;
import org.objectweb.asm.Opcodes;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * The type Pattern matched section.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class PatternMatchedSection {

  /**
   * The enum Access.
   */
  public enum Access {

    /**
     * Public access.
     */
    PUBLIC,
    /**
     * Protected access.
     */
    PROTECTED,
    /**
     * Friendly access.
     */
    FRIENDLY,
    /**
     * Private access.
     */
    PRIVATE,
    /**
     * None access.
     */
    NONE;

    /**
     * Is access level boolean.
     *
     * 
		 * @param level the level
     *
		 * @return the boolean
     */
    public boolean isAccessLevel( Access level ) {
//      System.out.println( "compare: " + this.compareTo( level ) );
      if( this.equals( NONE ) && (!level.equals( NONE )) ) return false;
      return ( this.compareTo( level ) >= 0 );
    }

    /**
     * Is access level boolean.
     *
     * 
		 * @param asmAccess the asm access
     *
		 * @return the boolean
     */
    public boolean isAccessLevel( int asmAccess ) {
      return isAccessLevel( Access.valueOf( asmAccess ) );
    }

    /**
     * Value of access.
     *
     * 
		 * @param asmAccess the asm access
     *
		 * @return the access
     */
    public static Access valueOf( int asmAccess ) {

      if ( ( asmAccess & Opcodes.ACC_PUBLIC ) == Opcodes.ACC_PUBLIC ) {
        return PUBLIC;
      } else if ( ( asmAccess & Opcodes.ACC_PROTECTED ) == Opcodes.ACC_PROTECTED ) {
        return PROTECTED;
      } else if ( ( asmAccess & Opcodes.ACC_PRIVATE ) == Opcodes.ACC_PRIVATE ) {
        return PRIVATE;
      } else {
        return FRIENDLY;
      }
    }
    
  }

  private Access access = null;

  /**
   * The Types.
   */
  protected Set<TypePatternSet.Type> types;

  /**
   * The Pattern sets.
   */
  protected Map<TypePatternSet.Type, PatternSet> patternSets;

  /**
   * Create pattern set type pattern set.
   *
   *
		 * @return the type pattern set
   */
  public TypePatternSet createPatternSet() {
    TypePatternSet typePatternSet = new TypePatternSet();
    addPatternSet( typePatternSet, typePatternSet.getType() );
    return typePatternSet;
  }

//  public void addConfiguredPatternSet( final TypePatternSet ps ) {
//    System.out.println( "PatternMatchedSection.addConfiguredPatternSet" );
//    addPatternSet( ps, ps.getType() );
//  }

  /**
   * Add pattern set.
   *
   * 
		 * @param ps   the ps
   * 
		 * @param type the type
   */
  public void addPatternSet( final PatternSet ps, TypePatternSet.Type type ) {
    if ( null == patternSets ) {
      patternSets = new EnumMap<TypePatternSet.Type, PatternSet>( TypePatternSet.Type.class );
    }

    // merge patternsets of same type
    if ( null != patternSets.get( type ) ) {
      PatternSet existing = patternSets.get( type );
      ps.addConfiguredPatternset( existing );
    }
    patternSets.put( type, ps );
  }

  /**
   * Gets pattern set.
   *
   * 
		 * @param type the type
   *
		 * @return the pattern set
   */
  public PatternSet getPatternSet( TypePatternSet.Type type ) {
    if ( null != patternSets ) {
      return patternSets.get( type );
    } else {
      return null;
    }
  }

  /**
   * Sets access.
   *
   * 
		 * @param access the access
   */
  public void setAccess( String access ) {
    this.access = Access.valueOf( access.toUpperCase() );
  }

  /**
   * Gets access.
   *
   *
		 * @return the access
   */
  public Access getAccess() {
    return access;
  }

  /**
   * Access value access.
   *
   * 
		 * @param accessString the access string
   *
		 * @return the access
   */
  protected Access accessValue( String accessString ) {
    Access access = null;
    if ( accessString.trim().equals( "" ) ) {
      throw new BuildException( "You specified an empty access modifier." );
    }
    try {
      access = Access.valueOf( accessString.trim().toUpperCase() );
    } catch ( java.lang.IllegalArgumentException e ) {
      throw new BuildException( "Illegal access modifier: " + accessString );
    }
    return access;
  }

}
