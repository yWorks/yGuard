package com.yworks.common.ant;

/** @author Michael Schroeder, yWorks GmbH http://www.yworks.com */
public abstract class Exclude {

  protected boolean source = false;
  protected boolean vtable = false;
  protected boolean ltable = false;
  protected boolean lttable = false;
  protected boolean rvAnn = true;
  protected boolean rvTypeAnn = true;
  protected boolean riAnn = false;
  protected boolean riTypeAnn = false;
  protected boolean rvPann = true;
  protected boolean riPann = false;
  protected boolean debugExtension = false;
  protected YGuardBaseTask task;


  public Exclude(YGuardBaseTask task) {
    this.task = task;
  }

  public void setSourcefile(boolean sf) {
    this.source = sf;
  }

  public void setLocalvariabletable(boolean vt) {
    this.vtable = vt;
  }

  public void setLinenumbertable(boolean lt) {
    this.ltable = lt;
  }

  public void setRuntimeVisibleAnnotations(boolean v) {
    this.rvAnn = v;
  }
  
  public void setRuntimeVisibleTypeAnnotations(boolean v) {
    this.rvTypeAnn = v;
  }

  public void setRuntimeInvisibleAnnotations(boolean v) {
      this.riAnn = v;
  }

  public void setRuntimeInvisibleTypeAnnotations(boolean v) {
    this.riTypeAnn = v;
  }
    
  public void setRuntimeVisibleParameterAnnotations(boolean v) {
    this.rvPann = v;
  }

  public void setRuntimeInvisibleParameterAnnotations(boolean v) {
    this.riPann = v;
  }

  public void setLocalVariableTypeTable(boolean lt) {
    this.lttable = lt;
  }

  public void setSourceDebugExtension(boolean b) {
    this.debugExtension = b;
  }

  public boolean isSource() {
    return source;
  }

  public boolean isVtable() {
    return vtable;
  }

  public boolean isLtable() {
    return ltable;
  }

  public boolean isLttable() {
    return lttable;
  }

  public boolean isRvAnn() {
    return rvAnn;
  }

  public boolean isRiAnn() {
    return riAnn;
  }

  public boolean isRvPann() {
    return rvPann;
  }

  public boolean isRiPann() {
    return riPann;
  }

  public boolean isDebugExtension() {
    return debugExtension;
  }

  /** Performs the check for circular references and returns the referenced PatternSet. */
//  protected Exclude getRef() {
//    if (!isChecked()) {
//      Stack stk = new Stack();
//      stk.push(this);
//      dieOnCircularReference(stk, task.getProject());
//    }
//
//    Object o = getRefid().getReferencedObject(task.getProject());
//    if (!(o instanceof Exclude)) {
//      String msg = getRefid().getRefId() + " doesn\'t denote a patternset";
//      throw new BuildException(msg);
//    } else {
//      return (Exclude) o;
//    }
//  }


}
