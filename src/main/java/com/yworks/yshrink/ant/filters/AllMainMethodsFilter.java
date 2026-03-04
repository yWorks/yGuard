package com.yworks.yshrink.ant.filters;

import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.logging.Logger;

/**
 * marks all main methods as entry points
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class AllMainMethodsFilter extends AbstractEntryPointFilter {

  /**
   * The Main desc.
   */
  static String MAIN_DESC = "([Ljava/lang/String;)V";

  @Override
  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {

    if ( "main".equals( md.getName() ) ) {
      Logger.log( "MainMethodFilter: main found in " + cd.getName() );
      return true;
    } else {
      return false;
    }
  }
}
