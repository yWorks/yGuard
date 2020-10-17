package com.yworks.yguard.ant;

import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.obf.YGuardRule;

import java.util.Collection;

/**
 * Used by ant to handle the <code>field</code> element.
 */
public final class FieldSection extends PatternMatchedClassesSection implements Mappable {
    private String name;
    private String className;
    private String mapTo;
  //private final YGuardBaseTask obfuscatorTask;



//  public FieldSection( YGuardBaseTask obfuscatorTask ) {
//    this.obfuscatorTask = obfuscatorTask;
//  }

    /**
     * Set name.
     *
     * @param name the name
     */
    public void setName(String name){
      this.name = name;
  }

    /**
     * Set class.
     *
     * @param name the name
     */
    public void setClass(String name){
      this.className = name;
    }

    public void addEntries( Collection entries, String className){
      String lname = ObfuscatorTask.toNativeClass(className)+'/'+name;
      YGuardRule entry = new YGuardRule(YGuardRule.TYPE_FIELD, lname);
      entries.add(entry);
    }

    /**
     * Set map.
     *
     * @param map the map
     */
    public void setMap(String map){
      this.mapTo = map;
    }

    public void addMapEntries(Collection entries)
    {
      String lname = ObfuscatorTask.toNativeClass(className)+'/'+name;
      YGuardRule entry = new YGuardRule(YGuardRule.TYPE_FIELD_MAP, lname);
      entry.obfName = mapTo;
      entries.add(entry);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * Gets class name.
     *
     * @return the class name
     */
    public String getClassName() {
      return className;
    }
}
