package com.yworks.yguard.ant;

import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DirectoryScanner;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

import com.yworks.yguard.ObfuscatorTask;
import com.yworks.common.ant.ant.ZipScannerTool;

/** Used as a super class for ant's handling of the
 * elements which can contain a <code>patternset</code> child element.
 */
public abstract class PatternMatchedClassesSection {
  protected List patternSets = new ArrayList(5);

  protected final Map properties = new HashMap();

  protected boolean allowMatchAllPatternSet = false;

  public void addConfiguredPatternSet( PatternSet ps){
    patternSets.add(ps);
  }

  public void addConfiguredProperty( Property p){
    this.properties.put(p.getName(), p.getValue());
  }

  public List getPatternSets() {
    return patternSets;
  }

  public void addEntries( Collection entries, ZipFileSet zf)throws IOException {
    Project project = zf.getProject();
    for ( Iterator it = patternSets.iterator(); it.hasNext();)
    {
      PatternSet ps = (PatternSet) it.next();
      DirectoryScanner scanner = zf.getDirectoryScanner(project);
      scanner.setIncludes( ObfuscatorTask.toNativePattern(ps.getIncludePatterns(project)));
      scanner.setExcludes(ObfuscatorTask.toNativePattern(ps.getExcludePatterns(project)));
      String[] matches = ZipScannerTool.getMatches(zf, scanner);
      for (int i = 0; i< matches.length; i++){
        String match = matches[i];
        if (match.endsWith(".class")){
          match = match.substring(0, match.length()-6);
          addEntries(entries, match);
        }
      }
    }
    if (patternSets.isEmpty() && allowMatchAllPatternSet){
      DirectoryScanner scanner = zf.getDirectoryScanner(project);
      scanner.setIncludes(new String[]{"**/*.class"});
      scanner.setExcludes(new String[0]);
      String[] matches = ZipScannerTool.getMatches(zf, scanner);
      for (int i = 0; i< matches.length; i++){
        String match = matches[i];
        if (match.endsWith(".class")){
          match = match.substring(0, match.length()-6);
          addEntries(entries, match);
        }
      }
    }
  }
  public abstract void addEntries(Collection entries, String matchedClass);
}
