package com.yworks.yshrink;

import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.EntryPointsSection;
import com.yworks.yguard.ant.ClassSection;
import com.yworks.yguard.ant.FieldSection;
import com.yworks.yguard.ant.MethodSection;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * The interface Y shrink invoker.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface YShrinkInvoker {

  /**
   * Execute.
   */
  void execute();

  /**
   * Add pair.
   *
   * @param pair the pair
   */
  void addPair( ShrinkBag pair );

  /**
   * Sets resource class path.
   *
   * @param path the path
   */
  void setResourceClassPath( Path path );

  /**
   * Add class section.
   *
   * @param cs the cs
   */
  void addClassSection( ClassSection cs );

  /**
   * Add method section.
   *
   * @param ms the ms
   */
  void addMethodSection( MethodSection ms );

  /**
   * Add field section.
   *
   * @param fs the fs
   */
  void addFieldSection( FieldSection fs );

  /**
   * Sets enty points.
   *
   * @param eps the eps
   */
  void setEntyPoints( EntryPointsSection eps );

  /**
   * Sets log file.
   *
   * @param shrinkLog the shrink log
   */
  void setLogFile( File shrinkLog );

  /**
   * Sets context.
   *
   * @param task the task
   */
  void setContext( Task task );
}
