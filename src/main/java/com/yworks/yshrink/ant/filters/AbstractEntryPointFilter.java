package com.yworks.yshrink.ant.filters;

import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;

/**
 * The type Abstract entry point filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class AbstractEntryPointFilter implements EntryPointFilter {

  public boolean isEntryPointClass( final Model model, final ClassDescriptor cd ) {
    return false;
  }

  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {
    return false;
  }

  public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd ) {
    return false;
  }

  public void setRetainAttribute( final ClassDescriptor cd ) {
    //
  }
}
