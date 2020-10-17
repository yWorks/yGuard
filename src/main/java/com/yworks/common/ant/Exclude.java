package com.yworks.common.ant;

/**
 * The type Exclude.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class Exclude {

  /**
   * The Source.
   */
  protected boolean source = false;
  /**
   * The Vtable.
   */
  protected boolean vtable = false;
  /**
   * The Ltable.
   */
  protected boolean ltable = false;
  /**
   * The Lttable.
   */
  protected boolean lttable = false;
  /**
   * The Rv ann.
   */
  protected boolean rvAnn = true;
  /**
   * The Rv type ann.
   */
  protected boolean rvTypeAnn = true;
  /**
   * The Ri ann.
   */
  protected boolean riAnn = false;
  /**
   * The Ri type ann.
   */
  protected boolean riTypeAnn = false;
  /**
   * The Rv pann.
   */
  protected boolean rvPann = true;
  /**
   * The Ri pann.
   */
  protected boolean riPann = false;
  /**
   * The Debug extension.
   */
  protected boolean debugExtension = false;
  /**
   * The Task.
   */
  protected YGuardBaseTask task;


  /**
   * Instantiates a new Exclude.
   *
   * @param task the task
   */
  public Exclude( YGuardBaseTask task ) {
    this.task = task;
  }

  /**
   * Sets sourcefile.
   *
   * @param sf the sf
   */
  public void setSourcefile( boolean sf ) {
    this.source = sf;
  }

  /**
   * Sets localvariabletable.
   *
   * @param vt the vt
   */
  public void setLocalvariabletable( boolean vt ) {
    this.vtable = vt;
  }

  /**
   * Sets linenumbertable.
   *
   * @param lt the lt
   */
  public void setLinenumbertable( boolean lt ) {
    this.ltable = lt;
  }

  /**
   * Sets runtime visible annotations.
   *
   * @param v the v
   */
  public void setRuntimeVisibleAnnotations( boolean v ) {
    this.rvAnn = v;
  }

  /**
   * Sets runtime visible type annotations.
   *
   * @param v the v
   */
  public void setRuntimeVisibleTypeAnnotations( boolean v ) {
    this.rvTypeAnn = v;
  }

  /**
   * Sets runtime invisible annotations.
   *
   * @param v the v
   */
  public void setRuntimeInvisibleAnnotations( boolean v ) {
    this.riAnn = v;
  }

  /**
   * Sets runtime invisible type annotations.
   *
   * @param v the v
   */
  public void setRuntimeInvisibleTypeAnnotations( boolean v ) {
    this.riTypeAnn = v;
  }

  /**
   * Sets runtime visible parameter annotations.
   *
   * @param v the v
   */
  public void setRuntimeVisibleParameterAnnotations( boolean v ) {
    this.rvPann = v;
  }

  /**
   * Sets runtime invisible parameter annotations.
   *
   * @param v the v
   */
  public void setRuntimeInvisibleParameterAnnotations( boolean v ) {
    this.riPann = v;
  }

  /**
   * Sets local variable type table.
   *
   * @param lt the lt
   */
  public void setLocalVariableTypeTable( boolean lt ) {
    this.lttable = lt;
  }

  /**
   * Sets source debug extension.
   *
   * @param b the b
   */
  public void setSourceDebugExtension( boolean b ) {
    this.debugExtension = b;
  }

  /**
   * Is source boolean.
   *
   * @return the boolean
   */
  public boolean isSource() {
    return source;
  }

  /**
   * Is vtable boolean.
   *
   * @return the boolean
   */
  public boolean isVtable() {
    return vtable;
  }

  /**
   * Is ltable boolean.
   *
   * @return the boolean
   */
  public boolean isLtable() {
    return ltable;
  }

  /**
   * Is lttable boolean.
   *
   * @return the boolean
   */
  public boolean isLttable() {
    return lttable;
  }

  /**
   * Is rv ann boolean.
   *
   * @return the boolean
   */
  public boolean isRvAnn() {
    return rvAnn;
  }

  /**
   * Is ri ann boolean.
   *
   * @return the boolean
   */
  public boolean isRiAnn() {
    return riAnn;
  }

  /**
   * Is rv pann boolean.
   *
   * @return the boolean
   */
  public boolean isRvPann() {
    return rvPann;
  }

  /**
   * Is ri pann boolean.
   *
   * @return the boolean
   */
  public boolean isRiPann() {
    return riPann;
  }

  /**
   * Is debug extension boolean.
   *
   * @return the boolean
   */
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
