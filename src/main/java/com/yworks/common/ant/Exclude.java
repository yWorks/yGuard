package com.yworks.common.ant;

/**
 * Stores which byte code attributes to keep when renaming.
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class Exclude {

  /**
   * Stores whether to keep the source file attribute.
   */
  protected boolean source = false;
  /**
   * Stores whether to keep the local variable table attribute.
   */
  protected boolean vtable = false;
  /**
   * Stores whether to keep the line number table attribute.
   */
  protected boolean ltable = false;
  /**
   * Stores whether to keep the local variable type table attribute.
   */
  protected boolean lttable = false;
  /**
   * Stores whether to keep the source debug extension attribute.
   */
  protected boolean debugExtension = false;
  /**
   * The task.
   */
  protected YGuardBaseTask task;


  /**
   * Initializes a new <code>Exclude</code> instance for the given task.
   * @param task the task
   */
  public Exclude(YGuardBaseTask task) {
    this.task = task;
  }

  /**
   * Sets whether to keep the source file attribute.
   * @param sf if <code>true</code>, source file attributes are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setSourcefile(boolean sf) {
    this.source = sf;
  }

  /**
   * Sets whether to keep the local variable table attribute.
   * @param vt if <code>true</code>, local variable table attributes are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setLocalvariabletable(boolean vt) {
    this.vtable = vt;
  }

  /**
   * Sets whether to keep the line number table attribute.
   * @param lt if <code>true</code>, line number table attributes are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setLinenumbertable(boolean lt) {
    this.ltable = lt;
  }

  /**
   * Sets whether to keep the local variable type table attribute.
   * @param lt if <code>true</code>, local variable type table attributes are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setLocalVariableTypeTable(boolean lt) {
    this.lttable = lt;
  }

  /**
   * Sets whether to keep the source debug extension attribute.
   * @param b if <code>true</code>, source debug extension attributes are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setSourceDebugExtension(boolean b) {
    this.debugExtension = b;
  }

  /**
   * Determines whether to keep the source file attribute.
   * @return <code>true</code> if source files attributes have to be
   * kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isSource() {
    return source;
  }

  /**
   * Determines whether to keep the local variable table attribute.
   * @return <code>true</code> if local variable table attributes have to be
   * kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isVtable() {
    return vtable;
  }

  /**
   * Determines whether to keep the line number table attribute.
   * @return <code>true</code> if line number table attributes have to be
   * kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isLtable() {
    return ltable;
  }

  /**
   * Determines whether to keep the local variable type table attribute.
   * @return <code>true</code> if local variable type table attributes have to
   * be kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isLttable() {
    return lttable;
  }

  /**
   * Determines whether to keep the source debug extension attribute.
   * @return <code>true</code> if source debug extension attributes have to be
   * kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isDebugExtension() {
    return debugExtension;
  }
}
