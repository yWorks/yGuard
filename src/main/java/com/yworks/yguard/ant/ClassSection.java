package com.yworks.yguard.ant;

import com.yworks.common.ant.YGuardBaseTask;
import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.obf.YGuardRule;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.ZipFileSet;

import java.io.IOException;
import java.util.Collection;

/**
 * Used by ant to handle the <code>class</code> element.
 */
public final class ClassSection extends PatternMatchedClassesSection implements Mappable {
  private String name;
  private String mapTo;
  private int methodMode = YGuardRule.LEVEL_NONE;
  private int fieldMode = YGuardRule.LEVEL_NONE;
  private int classMode = YGuardRule.LEVEL_NONE;
  private boolean classesSet = false;

  private String extendsType;
  private String implementsType;

  private final YGuardBaseTask task;

  /**
   * Instantiates a new Class section.
   */
  public ClassSection() {
    task = null;
  }

  /**
   * Instantiates a new Class section.
   *
   * @param task the task
   */
  public ClassSection( YGuardBaseTask task ) {
    this.task = task;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Sets classes.
   *
   * @param m the m
   */
  public void setClasses( ObfuscatorTask.Modifiers m ) {
    this.classMode = m.getModifierValue();
    this.classesSet = true;
  }

  /**
   * Sets methods.
   *
   * @param m the m
   */
  public void setMethods( ObfuscatorTask.Modifiers m ) {
    this.methodMode = m.getModifierValue();
  }

  /**
   * Sets fields.
   *
   * @param m the m
   */
  public void setFields( ObfuscatorTask.Modifiers m ) {
    fieldMode = m.getModifierValue();
  }

  /**
   * Sets map.
   *
   * @param mapTo the map to
   */
  public void setMap( String mapTo ) {
    this.mapTo = mapTo;
  }

  /**
   * Sets extends.
   *
   * @param extendsType the extends type
   */
  public void setExtends( String extendsType ) {
    this.extendsType = ObfuscatorTask.toNativeClass(extendsType);
    if (task instanceof ObfuscatorTask) {
      ((ObfuscatorTask) task).setNeedYShrinkModel(true);
    }

  }

  /**
   * Gets extends.
   *
   * @return the extends
   */
  public String getExtends() {
    return extendsType;
  }

  /**
   * Sets implements.
   *
   * @param implementsType the implements type
   */
  public void setImplements( String implementsType ) {
    this.implementsType = ObfuscatorTask.toNativeClass(implementsType);
    if (task instanceof ObfuscatorTask) {
      ((ObfuscatorTask) task).setNeedYShrinkModel(true);
    }
  }

  /**
   * Gets implements.
   *
   * @return the implements
   */
  public String getImplements() {
    return implementsType;
  }

  public void addEntries( Collection entries, ZipFileSet zf ) throws IOException {
    if (classesSet && patternSets.size() < 1) {
      PatternSet ps = new PatternSet();
      ps.setProject(zf.getProject());
      ps.setIncludes("**.*");
      patternSets.add(ps);
    }
    super.addEntries(entries, zf);
  }

  public void addEntries( Collection entries, String name ) {
    String className = ObfuscatorTask.toNativeClass(name);
    YGuardRule centry = new YGuardRule(YGuardRule.TYPE_CLASS, className);
    centry.retainFields = fieldMode;
    centry.retainMethods = methodMode;
    if (classesSet) {
      centry.retainClasses = classMode;
    }
    entries.add(centry);
  }

  public void addMapEntries( Collection entries ) {
    YGuardRule entry = new YGuardRule(YGuardRule.TYPE_CLASS_MAP, ObfuscatorTask.toNativeClass(name));
    entry.obfName = ObfuscatorTask.toNativeClass(mapTo);
    entries.add(entry);
  }

  /**
   * Gets class mode.
   *
   * @return the class mode
   */
  public int getClassMode() {
    return classMode;
  }

  /**
   * Gets field mode.
   *
   * @return the field mode
   */
  public int getFieldMode() {
    return fieldMode;
  }

  /**
   * Gets method mode.
   *
   * @return the method mode
   */
  public int getMethodMode() {
    return methodMode;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

}
