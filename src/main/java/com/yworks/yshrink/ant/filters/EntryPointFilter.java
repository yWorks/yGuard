package com.yworks.yshrink.ant.filters;

import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;

/**
 * The interface Entry point filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface EntryPointFilter {

    /**
     * Is entry point class boolean.
     *
     * @param model the model
     * @param cd    the cd
     * @return the boolean
     */
    public boolean isEntryPointClass( final Model model, final ClassDescriptor cd );

    /**
     * Is entry point method boolean.
     *
     * @param model the model
     * @param cd    the cd
     * @param md    the md
     * @return the boolean
     */
    public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md );

    /**
     * Is entry point field boolean.
     *
     * @param model the model
     * @param cd    the cd
     * @param fd    the fd
     * @return the boolean
     */
    public boolean isEntryPointField( final Model model, final ClassDescriptor cd, final FieldDescriptor fd );

    /**
     * Sets retain attribute.
     *
     * @param cd the cd
     */
    public void setRetainAttribute( final ClassDescriptor cd );

}
