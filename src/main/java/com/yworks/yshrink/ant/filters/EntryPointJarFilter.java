package com.yworks.yshrink.ant.filters;

import com.yworks.common.ant.EntryPointJar;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;

/**
 * The type Entry point jar filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class EntryPointJarFilter extends AbstractEntryPointFilter {

  private final EntryPointJar entryPointJar;

    /**
     * Instantiates a new Entry point jar filter.
     *
     * @param entryPointJar the entry point jar
     */
    public EntryPointJarFilter( EntryPointJar entryPointJar ) {
    this.entryPointJar = entryPointJar;
  }

  @Override
  public boolean isEntryPointClass( final Model model, final ClassDescriptor cd ) {
    return cd.getSourceJar().equals( entryPointJar.getIn() );
  }

  @Override
  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {
    return md.getSourceJar().equals( entryPointJar.getIn() );
  }

  @Override
  public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd ) {
    return fd.getSourceJar().equals( entryPointJar.getIn() );
  }
}
