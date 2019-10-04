package com.yworks.yguard;

import com.yworks.yguard.common.ShrinkBag;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.io.IOException;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface YShrinkModel {

  public void createSimpleModel( List<ShrinkBag> bags ) throws IOException;

  Set<String> getAllAncestorClasses( String className );

  Set<String> getAllImplementedInterfaces( String className );

  Collection<String> getAllClassNames();

  void setResourceClassPath(Path resourceClassPath, Task target);
}
