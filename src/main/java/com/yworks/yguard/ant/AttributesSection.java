package com.yworks.yguard.ant;

import com.yworks.yguard.obf.YGuardRule;
import com.yworks.common.ant.YGuardBaseTask;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * Used by ant to handle the <code>attributes</code> element.
 */
public final class AttributesSection extends PatternMatchedClassesSection implements Mappable {
  private YGuardBaseTask obfuscatorTask;

    /**
     * Instantiates a new Attributes section.
     *
     *
		 * @param obfuscatorTask the obfuscator task
     */
    public AttributesSection( YGuardBaseTask obfuscatorTask ){
    super();
    this.obfuscatorTask = obfuscatorTask;
    this.allowMatchAllPatternSet = true;
  }

    private String attributes;

    /**
     * Set name.
     *
     *
		 * @param attributes the attributes
     */
    public void setName(String attributes){
        this.attributes = attributes;
    }

    public void addEntries( Collection entries, String className){
      StringTokenizer st = new StringTokenizer(this.attributes, ", ", false);
      while (st.hasMoreTokens()){
        String token = st.nextToken().trim();
        YGuardRule entry = new YGuardRule(YGuardRule.TYPE_ATTR2, token, className);
        entries.add(entry);
      }
    }

    public void addMapEntries(Collection entries)
    {
    }

    /**
     * Gets attributes.
     *
     *
		 * @return the attributes
     */
    public String getAttributes() {
    return attributes;
  }
}
