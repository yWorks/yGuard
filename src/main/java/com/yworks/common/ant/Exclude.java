package com.yworks.common.ant;

/**
 * Stores which byte code attributes to keep when renaming or shrinking. 
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
   * Stores whether to keep the runtime visible annotations.
   */
  protected boolean rvAnn = true;
  /**
   * Stores whether to keep the runtime visible type annotations.
   */
  protected boolean rvTypeAnn = true;
  /**
   * Stores whether to keep the runtime invisible annotations.
   */
  protected boolean riAnn = false;
  /**
   * Stores whether to keep the runtime invisible type annotations.
   */
  protected boolean riTypeAnn = false;
  /**
   * Stores whether to keep the runtime visible parameter annotations.
   */
  protected boolean rvPann = true;
  /**
   * Stores whether to keep the runtime invisible parameter annotations.
   */
  protected boolean riPann = false;
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
   * Sets whether to keep the runtime visible annotations.
   * @param v if <code>true</code>, runtime visible annotations are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setRuntimeVisibleAnnotations(boolean v) {
    this.rvAnn = v;
  }

  /**
   * Sets whether to keep the runtime visible type annotations.
   * @param v if <code>true</code>, runtime visible type annotations are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setRuntimeVisibleTypeAnnotations(boolean v) {
    this.rvTypeAnn = v;
  }

  /**
   * Sets whether to keep the runtime invisible annotations.
   * @param v if <code>true</code>, runtime invisible annotations are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setRuntimeInvisibleAnnotations(boolean v) {
    this.riAnn = v;
  }

  /**
   * Sets whether to keep the runtime invisible type annotations.
   * @param v if <code>true</code>, runtime invisible type annotations are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setRuntimeInvisibleTypeAnnotations(boolean v) {
    this.riTypeAnn = v;
  }

  /**
   * Sets whether to keep the runtime visible parameter annotations.
   * @param v if <code>true</code>, runtime visible parameter annotations are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setRuntimeVisibleParameterAnnotations(boolean v) {
    this.rvPann = v;
  }

  /**
   * Sets whether to keep the runtime invisible parameter annotations.
   * @param v if <code>true</code>, runtime invisible parameter annotations are
   * kept when renaming or shrinking, otherwise they are removed.
   */
  public void setRuntimeInvisibleParameterAnnotations(boolean v) {
    this.riPann = v;
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
   * Determines whether to keep the runtime visible annotations.
   * @return <code>true</code> if runtime visible annotations have to be
   * kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isRvAnn() {
    return rvAnn;
  }

  /**
   * Determines whether to keep the runtime invisible annotations.
   * @return <code>true</code> if runtime invisible annotations have to be
   * kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isRiAnn() {
    return riAnn;
  }

  /**
   * Determines whether to keep the runtime visible parameter annotations.
   * @return <code>true</code> if runtime visible parameter annotations have to
   * be kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isRvPann() {
    return rvPann;
  }

  /**
   * Determines whether to keep the runtime invisible parameter annotations.
   * @return <code>true</code> if runtime invisible parameter annotations have
   * to be kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isRiPann() {
    return riPann;
  }

  /**
   * Determines whether to keep the runtime visible type annotations.
   * @return <code>true</code> if runtime visible type annotations have
   * to be kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isRvTypeAnn() {
    return rvTypeAnn;
  }

  /**
   * Determines whether to keep the runtime invisible type annotations.
   * @return <code>true</code> if runtime invisible type annotations have
   * to be kept when renaming or shrinking, <code>false</code> otherwise. 
   */
  public boolean isRiTypeAnn() {
    return riTypeAnn;
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
