package com.yworks.yguard;

import com.yworks.common.ant.EntryPointsSection;
import com.yworks.common.ShrinkBag;
import com.yworks.yguard.ant.MethodSection;
import com.yworks.yguard.ant.ClassSection;
import com.yworks.yguard.ant.FieldSection;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface YShrinkInvoker {

  public void execute();

  public void addPair( ShrinkBag pair );

  public void setResourceClassPath( Path path );

  public void addClassSection( ClassSection cs );

  void addMethodSection( MethodSection ms );

  void addFieldSection( FieldSection fs );

  void setEntyPoints( EntryPointsSection eps );

  void setLogFile( File shrinkLog );

  void setContext(Task task);
}
