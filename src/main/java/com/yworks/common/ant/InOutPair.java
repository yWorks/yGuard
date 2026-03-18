package com.yworks.common.ant;

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

  public String toString() {
    return "in: " + inFile + "; out: " + outFile;
  }
}
