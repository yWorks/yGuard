package com.yworks.yguard;

import com.yworks.yguard.obf.Cl;
import com.yworks.common.ShrinkBag;
import com.yworks.common.ant.*;
import com.yworks.common.ant.AttributesSection;
import com.yworks.yshrink.ant.ShrinkTask;
import com.yworks.logging.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The type Y guard task.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class YGuardTask extends YGuardBaseTask {

  private List<YGuardBaseTask> subTasks = new ArrayList<YGuardBaseTask>();

  @Override
  public void execute() throws BuildException {

    super.execute();

    // inoutpairs
    if ( null != pairs ) {
      for ( ShrinkBag pair : pairs ) {
        for ( YGuardBaseTask subTask : subTasks ) {
          subTask.addConfiguredInOutPair( pair );
        }
      }
    } else {
      throw new BuildException("No inoutpairs given. At least one inoutpair has to be specified.");
    }

    // externalclasses
    if ( null != resourceClassPath ) {
      for ( YGuardBaseTask subTask : subTasks ) {
        subTask.setResourceClassPath( resourceClassPath );
      }
    }

    // attributes
    if ( null != attributesSections ) {
      for ( YGuardBaseTask subTask : subTasks ) {
        subTask.addAttributesSections( attributesSections );
      }
    }

    // exclude
//    if ( null != exclude ) {
//      for ( YGuardBaseTask subTask : subTasks ) {
//        if ( subTask instanceof ObfuscatorTask ) {
//          subTask.
//        }
//      }
//    }

    // execute ShrinkTask first

    

    Collections.sort(
        subTasks,
        new Comparator<YGuardBaseTask>() {
          public int compare( YGuardBaseTask o1, YGuardBaseTask o2 ) {
            if ( o1 instanceof ShrinkTask ) {
              return 0;
            }
            return 1;
          }
        }
    );


    // execute
    int taskNum = 0;
    File[] outFiles  = new File[ pairs.size() ];
    File[] tempFiles = new File[ pairs.size() ];

    for ( YGuardBaseTask subTask : subTasks ) {

      for ( int i = 0; i < pairs.size(); i++ ) {
        ShrinkBag pair = pairs.get( i );

        if ( 0 == taskNum ) {
          outFiles[ i ] = pair.getOut();
        } else {
          if ( taskNum > 1 ) {
            pair.getIn().delete();
          }
          pair.setIn( pair.getOut() );
        }

        if ( taskNum == ( subTasks.size() - 1 ) ) {
          pair.setOut( outFiles[ i ] );
        } else {
          File tempFile = getTempFile( pair.getOut() );
          tempFiles[ i ] = tempFile;
          pair.setOut( tempFile );
        }

        if ( taskNum > 1 ) {
          tempFiles[ ( taskNum * ( pairs.size() - 1 ) ) + i ].delete();
        }
      }

     //getProject().log( "executing subtask "+subTask.getClass().getName(), Project.MSG_INFO );

      subTask.execute();

      taskNum++;
    }

    if ( subTasks.size() > 1 ) {
      for ( File tempFile : tempFiles ) {
        tempFile.delete();
      }
    }

    Cl.setClassResolver(null);

  }

  private File getTempFile( File origFile ) {
    try {
      File folder = new File( origFile.getParent() );
      if (folder.exists()) {
        File tempFile = File.createTempFile("yguard_temp_", ".jar", folder);
        tempFile.deleteOnExit();
        return tempFile;
      } else {
        System.out.println("could not create temp file for " + origFile +" - parent folder does not exist: "+folder);
        return null;
      }

    } catch ( IOException e ) {
      Logger.err("could not create temp file for " + origFile, e);
      throw new BuildException( "could not create temp file for " + origFile );
    }
  }

  /**
   * Create shrink shrink task.
   *
   * 
		 * @return the shrink task
   */
  public ShrinkTask createShrink() {
    ShrinkTask shrinkTask = new ShrinkTask( YGuardBaseTask.MODE_NESTED );
    configureSubTask(shrinkTask);
    subTasks.add( shrinkTask );
    return shrinkTask;
  }

  /**
   * Create rename obfuscator task.
   *
   * 
		 * @return the obfuscator task
   */
  public ObfuscatorTask createRename() {
    ObfuscatorTask obfuscatorTask = new ObfuscatorTask( YGuardBaseTask.MODE_NESTED );
    configureSubTask(obfuscatorTask);
    subTasks.add( obfuscatorTask );
    return obfuscatorTask;
  }

  private void configureSubTask(Task task) {
    task.setProject(getProject());
    task.setOwningTarget(getOwningTarget());
    task.setTaskName(getTaskName());
    task.setLocation(getLocation());
    task.setDescription(getDescription());
    task.init();
  }

  /**
   * Create obfuscate obfuscator task.
   *
   * 
		 * @return the obfuscator task
   */
  public ObfuscatorTask createObfuscate() {
    return createRename();
  }

  public Exclude createKeep() {
    throw new BuildException("The keep element is allowed only in nested subtasks of the yguard task.");
  }

  public void addAttributesSections( List<AttributesSection> attributesSections ) {
  }
}
