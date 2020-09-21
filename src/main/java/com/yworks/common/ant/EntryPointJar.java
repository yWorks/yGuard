package com.yworks.common.ant;

import com.yworks.common.ResourcePolicy;
import com.yworks.common.ShrinkBag;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class EntryPointJar implements ShrinkBag {

  private File inFile;

  public void setIn( File file ) {
    inFile = file;
  }

  public void setName(File fileName) {
    inFile = fileName;
  }

  public void setOut( File file ) {
    throw new BuildException( "You can't set an outfile on an EntryPointJar." );
  }

  public File getIn() {
    return inFile;
  }

  public File getOut() {
    return null;
  }

  public boolean isEntryPointJar() {
    return true;
  }

  public void setResources( String resourcesStr ) {
    throw new BuildException( "You can't set resources on an EntryPointJar." );
  }

  public ResourcePolicy getResources() {
    return null;
  }
}
