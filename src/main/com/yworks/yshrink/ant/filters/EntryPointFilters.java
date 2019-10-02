package com.yworks.yshrink.ant.filters;

import com.yworks.yguard.common.ant.Exclude;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class EntryPointFilters extends AbstractEntryPointFilter {

  List<EntryPointFilter> filters;

  private Exclude exclude;

  public EntryPointFilters() {
    this.filters = new ArrayList<EntryPointFilter>();
  }

  public void setExclude( Exclude exclude ) {
    this.exclude = exclude;
  }

  public void addEntryPointFilter( final EntryPointFilter entryPointFilter ) {
    filters.add( entryPointFilter );
  }

  public boolean isEntryPointClass( final Model model, final ClassDescriptor cd ) {
    for ( EntryPointFilter entryPointFilter : filters ) {
      if ( entryPointFilter.isEntryPointClass( model, cd ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {
    for ( EntryPointFilter entryPointFilter : filters ) {
      if ( entryPointFilter.isEntryPointMethod( model, cd, md ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd ) {
    for ( EntryPointFilter entryPointFilter : filters ) {
      if ( entryPointFilter.isEntryPointField( model, cd, fd ) ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void setRetainAttribute( final ClassDescriptor cd ) {
    for ( EntryPointFilter entryPointFilter : filters ) {
      entryPointFilter.setRetainAttribute( cd );
    }
  }
}
