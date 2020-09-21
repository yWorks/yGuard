package com.yworks.yguard.ant;

import com.yworks.yguard.obf.YGuardRule;
import com.yworks.yguard.obf.LineNumberTableMapper;
import com.yworks.yguard.obf.classfile.LineNumberTableAttrInfo;
import com.yworks.yguard.ObfuscatorTask;
import com.yworks.common.ant.YGuardBaseTask;

import java.util.Collection;
import java.io.PrintWriter;

/** Used by ant to handle the <code>attributes</code> element.
 */
public final class LineNumberTableSection extends PatternMatchedClassesSection implements Mappable {
  private YGuardBaseTask obfuscatorTask;

  public LineNumberTableSection( YGuardBaseTask obfuscatorTask ){
    super();
    this.obfuscatorTask = obfuscatorTask;
    this.allowMatchAllPatternSet = true;
  }

    public void addEntries( Collection entries, String className){
      YGuardRule rule = createRule(className);
      entries.add(rule);
    }

  private LineNumberTableMapper mapper;
  private YGuardRule createRule(String className) {
    if (mapper == null){
      mapper = createMapper();
    }
    return new YGuardRule(className, mapper);
  }

  private LineNumberTableMapper createMapper() {
    LineNumberTableMapper lntMapper = null;
    if (properties.containsKey("mapping-scheme")){
      String ms = (String) properties.get("mapping-scheme");
      if ("squeeze".equals(ms)){
        lntMapper = new ObfuscatorTask.LineNumberSqueezer();
      } else if ("scramble".equals(ms)){
        long saltValue = (long) (Math.random() * 4242L);
        if (properties.containsKey("scrambling-salt")){
          String salt = (String) properties.get("scrambling-salt");
          try {
            saltValue = Long.parseLong(salt);
          } catch (NumberFormatException e) {
            // ignore
          }
        }
        lntMapper = new ObfuscatorTask.MyLineNumberTableMapper(saltValue);
//          getProject().log(this, "Using Line Number Scrambling with Salt " + saltValue + ".", Project.MSG_INFO);
      } else {
//           getProject().log(this, "Unknown mapping-scheme " + ms + "!", Project.MSG_ERR);
      }
    }
    if (lntMapper == null) {
      lntMapper = new LineNumberTableMapper() {
        public boolean mapLineNumberTable(String className, String methodName, String methodSignature, LineNumberTableAttrInfo lineNumberTable) {
          return true;
        }

        public void logProperties( PrintWriter pw) {
          return;
        }
      };
    }
    return lntMapper;
  }

  public void addMapEntries(Collection entries)
  {
  }
}
