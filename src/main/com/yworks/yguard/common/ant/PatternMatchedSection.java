package com.yworks.yguard.common.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.PatternSet;
import org.objectweb.asm.Opcodes;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class PatternMatchedSection {

  public enum Access {

    PUBLIC,PROTECTED,FRIENDLY,PRIVATE,NONE;

    public boolean isAccessLevel( Access level ) {
//      System.out.println( "compare: " + this.compareTo( level ) );
      if( this.equals( NONE ) && (!level.equals( NONE )) ) return false;
      return ( this.compareTo( level ) >= 0 );
    }

    public boolean isAccessLevel( int asmAccess ) {
      return isAccessLevel( Access.valueOf( asmAccess ) );
    }

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

  protected Set<TypePatternSet.Type> types;

  protected Map<TypePatternSet.Type, PatternSet> patternSets;
  
  public TypePatternSet createPatternSet() {
    TypePatternSet typePatternSet = new TypePatternSet();
    addPatternSet( typePatternSet, typePatternSet.getType() );
    return typePatternSet;
  }

//  public void addConfiguredPatternSet( final TypePatternSet ps ) {
//    System.out.println( "PatternMatchedSection.addConfiguredPatternSet" );
//    addPatternSet( ps, ps.getType() );
//  }

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

  public PatternSet getPatternSet( TypePatternSet.Type type ) {
    if ( null != patternSets ) {
      return patternSets.get( type );
    } else {
      return null;
    }
  }

  public void setAccess( String access ) {
    this.access = Access.valueOf( access.toUpperCase() );
  }

  public Access getAccess() {
    return access;
  }

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
