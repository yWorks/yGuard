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
import com.yworks.common.ant.ZipScannerTool;

/**
 * Used as a super class for ant's handling of the
 * elements which can contain a <code>patternset</code> child element.
 */
public abstract class PatternMatchedClassesSection {
  /**
   * The Pattern sets.
   */
  protected List patternSets = new ArrayList(5);

  /**
   * The Properties.
   */
  protected final Map properties = new HashMap();

  /**
   * The Allow match all pattern set.
   */
  protected boolean allowMatchAllPatternSet = false;

  /**
   * Add configured pattern set.
   *
   * @param ps the ps
   */
  public void addConfiguredPatternSet( PatternSet ps){
    patternSets.add(ps);
  }

  /**
   * Add configured property.
   *
   * @param p the p
   */
  public void addConfiguredProperty( Property p){
    this.properties.put(p.getName(), p.getValue());
  }

  /**
   * Gets pattern sets.
   *
   * @return the pattern sets
   */
  public List getPatternSets() {
    return patternSets;
  }

  /**
   * Add entries.
   *
   * @param entries the entries
   * @param zf      the zf
   * @throws IOException the io exception
   */
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

  /**
   * Add entries.
   *
   * @param entries      the entries
   * @param matchedClass the matched class
   */
  public abstract void addEntries(Collection entries, String matchedClass);
}
