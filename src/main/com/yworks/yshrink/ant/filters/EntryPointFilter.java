package com.yworks.yshrink.ant.filters;

import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface EntryPointFilter {

  public boolean isEntryPointClass( final Model model, final ClassDescriptor cd );

  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md );

  public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd );

  public void setRetainAttribute( final ClassDescriptor cd );

}
