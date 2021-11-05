package com.yworks.yguard.ant;

import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yguard.obf.classfile.ClassConstants;
import com.yworks.common.ant.YGuardBaseTask;

import java.util.Collection;

/**
 * Used by ant to handle the <code>attributes</code> element.
 */
public class SourceFileSection extends PatternMatchedClassesSection implements Mappable {
  protected final YGuardBaseTask obfuscatorTask;

  /**
   * Instantiates a new Source file section.
   *
   * @param obfuscatorTask the obfuscator task
   */
  public SourceFileSection( YGuardBaseTask obfuscatorTask ){
    super();
    this.obfuscatorTask = obfuscatorTask;
    this.allowMatchAllPatternSet = true;
  }

  public void addEntries( Collection entries, String className){
    YGuardRule rule = createRule(className);
    entries.add(rule);
  }

  private YGuardRule createRule(String className) {
    if (properties.containsKey("mapping")){
      YGuardRule sourceRule = new YGuardRule(YGuardRule.TYPE_SOURCE_ATTRIBUTE_MAP, className);
      sourceRule.obfName = (String) properties.get("mapping");
      return sourceRule;
    } else {
      YGuardRule rule = new YGuardRule(YGuardRule.TYPE_ATTR2, ClassConstants.ATTR_SourceFile, className);
      return rule;
    }
  }

  public void addMapEntries(Collection entries)
  {
  }
}
