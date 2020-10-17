package com.yworks.common.ant;

import com.yworks.common.ResourcePolicy;
import com.yworks.common.ShrinkBag;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * The type In out pair.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class InOutPair implements ShrinkBag {
    private File inFile;
    private File outFile;

  /**
   * The Resources.
   */
  ResourcePolicy resources = ResourcePolicy.COPY;

    public void setIn( final File file ) {
      this.inFile = file;
    }

    public void setOut( final File file ) {
      this.outFile = file;
    }

    public File getIn() {
      return inFile;
    }

    public File getOut() {
      return outFile;
    }

    public boolean isEntryPointJar() {
      return false;
    }

    public void setResources( String resourcesStr ) {

      try {
        resources = ResourcePolicy.valueOf( resourcesStr.trim().toUpperCase() );
      } catch ( IllegalArgumentException e ) {
        throw new BuildException( "Invalid resource policy: " + resourcesStr );
      }
    }

    public ResourcePolicy getResources() {
      return resources;
    }

    public String toString() {
      return "in: " + inFile + "; out: " + outFile;
    }
  }
