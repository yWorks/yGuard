package com.yworks.yguard.ant;

import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.common.ant.YGuardBaseTask;
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

  public ClassSection() {
    task = null;
  }

  public ClassSection( YGuardBaseTask task ) {
    this.task = task;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public void setClasses( ObfuscatorTask.Modifiers m ) {
    this.classMode = m.getModifierValue();
    this.classesSet = true;
  }

  public void setMethods( ObfuscatorTask.Modifiers m ) {
    this.methodMode = m.getModifierValue();
  }

  public void setFields( ObfuscatorTask.Modifiers m ) {
    fieldMode = m.getModifierValue();
  }

  public void setMap( String mapTo ) {
    this.mapTo = mapTo;
  }

  public void setExtends( String extendsType ) {
    this.extendsType = ObfuscatorTask.toNativeClass( extendsType );
    if ( task instanceof ObfuscatorTask ) {
      ( (ObfuscatorTask) task ).setNeedYShrinkModel( true );
    }

  }

  public String getExtends() {
    return extendsType;
  }

  public void setImplements( String implementsType ) {
    this.implementsType = ObfuscatorTask.toNativeClass( implementsType );
    if ( task instanceof ObfuscatorTask ) {
      ( (ObfuscatorTask) task ).setNeedYShrinkModel( true );
    }
  }

  public String getImplements() {
    return implementsType;
  }

  public void addEntries( Collection entries, ZipFileSet zf ) throws IOException {
    if ( classesSet && patternSets.size() < 1 ) {
      PatternSet ps = new PatternSet();
      ps.setProject( zf.getProject() );
      ps.setIncludes( "**.*" );
      patternSets.add( ps );
    }
    super.addEntries( entries, zf );
  }

  public void addEntries( Collection entries, String name ) {
    String className = ObfuscatorTask.toNativeClass( name );
    YGuardRule centry = new YGuardRule( YGuardRule.TYPE_CLASS, className );
    centry.retainFields = fieldMode;
    centry.retainMethods = methodMode;
    if ( classesSet ) {
      centry.retainClasses = classMode;
    }
    entries.add( centry );
  }

  public void addMapEntries( Collection entries ) {
    YGuardRule entry = new YGuardRule( YGuardRule.TYPE_CLASS_MAP, ObfuscatorTask.toNativeClass( name ) );
    entry.obfName = ObfuscatorTask.toNativeClass( mapTo );
    entries.add( entry );
  }

  public int getClassMode() {
    return classMode;
  }

  public int getFieldMode() {
    return fieldMode;
  }

  public int getMethodMode() {
    return methodMode;
  }

  public String getName() {
    return name;
  }

}
