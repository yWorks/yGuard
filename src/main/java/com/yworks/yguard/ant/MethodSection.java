package com.yworks.yguard.ant;

import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.common.ant.YGuardBaseTask;

import java.util.Collection;

/** Used by ant to handle the <code>method</code> element.
 */
public final class MethodSection extends PatternMatchedClassesSection implements Mappable {
    private String name;
    private String className;
    private String mapTo;

//  private final YGuardBaseTask task;

//  public MethodSection( YGuardBaseTask task ) {
//    this.task = task;
//  }

  public void setName(String name){
        this.name = name;
    }
    public void setClass(String name){

      this.className = name;
    }

    public void setMap(String mapTo){
      this.mapTo = mapTo;
    }

    public void addEntries( Collection entries, String className){
      String[] method = ObfuscatorTask.toNativeMethod(name);
      String name = ObfuscatorTask.toNativeClass(className)+'/'+method[0];
      String descriptor = method[1];
      YGuardRule entry = new YGuardRule(YGuardRule.TYPE_METHOD, name, descriptor);
      entries.add(entry);
    }

    public void addMapEntries(Collection entries)
    {
      String[] method = ObfuscatorTask.toNativeMethod(name);
      String name = ObfuscatorTask.toNativeClass(className)+'/'+method[0];
      String descriptor = method[1];
      YGuardRule entry = new YGuardRule(YGuardRule.TYPE_METHOD_MAP, name, descriptor);
      entry.obfName = mapTo;
      entries.add(entry);
    }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }
}
