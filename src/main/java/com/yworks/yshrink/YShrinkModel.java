package com.yworks.yshrink;

import com.yworks.common.ShrinkBag;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * The interface Y shrink model.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface YShrinkModel {

    /**
     * Create simple model.
     *
     * @param bags the bags
     * @throws IOException the io exception
     */
    public void createSimpleModel( List<ShrinkBag> bags ) throws IOException;

    /**
     * Gets all ancestor classes.
     *
     * @param className the class name
     * @return the all ancestor classes
     */
    Set<String> getAllAncestorClasses( String className );

    /**
     * Gets all implemented interfaces.
     *
     * @param className the class name
     * @return the all implemented interfaces
     */
    Set<String> getAllImplementedInterfaces( String className );

    /**
     * Gets all class names.
     *
     * @return the all class names
     */
    Collection<String> getAllClassNames();

    /**
     * Sets resource class path.
     *
     * @param resourceClassPath the resource class path
     * @param target            the target
     */
    void setResourceClassPath(Path resourceClassPath, Task target);
}
