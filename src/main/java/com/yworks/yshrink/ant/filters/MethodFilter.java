package com.yworks.yshrink.ant.filters;

import com.yworks.common.ant.TypePatternSet;
import com.yworks.yshrink.ant.MethodSection;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.util.Util;
import org.apache.tools.ant.Project;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class MethodFilter extends PatternMatchedFilter {

  private List<MethodSection> sections;

  public MethodFilter( Project project ) {
    super( project );
  }

  public void addMethodSection( MethodSection methodSection ) {
    if ( null == sections ) {
      sections = new ArrayList<MethodSection>( 5 );
    }
    sections.add( methodSection );
  }

  @Override
  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {

    String className = cd.getName();
    String methodName = md.getName();

    for ( MethodSection ms : sections ) {

      String entryMethodName = ms.getName();
      String entryMethodClass = ms.getClassName();

      boolean r = true;

      // returnType
      if ( null != ms.getReturnType() ) {
        Type requiredReturnType = Type.getType( Util.verboseToNativeType( ms.getReturnType() ) );
        r &= ( requiredReturnType.equals( md.getReturnType() ) );
      }

      // arguments
      if ( null != ms.getArgs() ) {
        String[] requiredArgTypes = ms.getArgs().split( "\\s*,\\s*" );
        if ( requiredArgTypes.length == 1 && requiredArgTypes[ 0 ].length() == 0 ) { // args=""
          requiredArgTypes = new String[0];
        }
        Type[] argTypes = md.getArgumentTypes();

        if ( requiredArgTypes.length == argTypes.length ) {
          for ( int i = 0; i < argTypes.length; i++ ) {
            Type argType = argTypes[ i ];
            Type requiredArgType = Type.getType( Util.verboseToNativeType( requiredArgTypes[ i ].trim() ) );

            r &= argType.equals( requiredArgType );
          }
        } else {
          r = false;
        }
      }

      // access
      if ( null != ms.getAccess() ) {
        r &= ms.getAccess().isAccessLevel( md.getAccess() );
      }

      // class
      if ( null == entryMethodClass || entryMethodClass.length() == 0 ) {
        r &= match( TypePatternSet.Type.CLASS, className, ms ) ||
                match( TypePatternSet.Type.CLASS, Util.toJavaClass( className ), ms );
      } else {
        r &= entryMethodClass.equals( className );
      }

      // throws
      if ( null != ms.getThrows() ) {

        StringTokenizer tokenizer = new StringTokenizer( ms.getThrows(), "," );

        while ( tokenizer.hasMoreTokens() ) {
          String exception = Util.toInternalClass( tokenizer.nextToken().trim() );



          boolean found = false;

          if ( null != md.getExceptions() ) {
            for ( String exception2 : md.getExceptions() ) {
              if ( exception2.equals( exception ) ) {
                found = true;
              }
            }
          }

          r &= found;

        }
      }

      // name
      if ( null == entryMethodName || entryMethodName.length() == 0 ) {
        r &= match( TypePatternSet.Type.NAME, methodName, ms );
      } else {
        r &= entryMethodName.equals( methodName );
      }

      if ( r ) {
        return r;
      }
    }

    return false;
  }
}
