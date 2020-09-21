package com.yworks.yguard.ant;

import com.yworks.common.ant.ant.ZipScannerTool;
import com.yworks.yguard.ObfuscatorTask;
import com.yworks.yguard.obf.YGuardRule;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.ZipFileSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

/** Used by ant to handle the <code>package</code> element. */
public final class PackageSection implements Mappable {
  private String name;
  private String mapTo;
  protected List patternSets = new ArrayList(5);

  protected boolean allowMatchAllPatternSet = false;

  public void addConfiguredPatternSet(PatternSet ps) {
    patternSets.add(ps);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addEntries(Collection entries, ZipFileSet zf) throws IOException {
    Project project = zf.getProject();
    Set packages = new HashSet();
    if (name != null) {
      packages.add(ObfuscatorTask.toNativeClass(name));
    }
    for (Iterator it = patternSets.iterator(); it.hasNext();) {
      PatternSet ps = (PatternSet) it.next();
      DirectoryScanner scanner = zf.getDirectoryScanner(project);
      scanner.setIncludes(ObfuscatorTask.toNativePattern(ps.getIncludePatterns(project)));
      scanner.setExcludes(ObfuscatorTask.toNativePattern(ps.getExcludePatterns(project)));
      String[] matches = ZipScannerTool.getMatches(zf, scanner);
      for (int i = 0; i < matches.length; i++) {
        String match = matches[i];
        int slashIndex = match.lastIndexOf('/');
        if (match.endsWith(".class") || match.endsWith("/") && slashIndex > 0) {
          match = match.substring(0, slashIndex);
          packages.add(match);
        }
      }
    }
    if (patternSets.isEmpty() && allowMatchAllPatternSet && name == null) {
      DirectoryScanner scanner = zf.getDirectoryScanner(project);
      scanner.setIncludes(new String[]{"**/*.class"});
      scanner.setExcludes(new String[0]);
      String[] matches = ZipScannerTool.getMatches(zf, scanner);
      for (int i = 0; i < matches.length; i++) {
        String match = matches[i];
        int slashIndex = match.lastIndexOf('/');
        if (match.endsWith(".class") || match.endsWith("/") && slashIndex > 0) {
          match = match.substring(0, slashIndex);
          packages.add(match);
        }
      }
    }

    for (Iterator iterator = packages.iterator(); iterator.hasNext();) {
      String pack = (String) iterator.next();
      YGuardRule rule = new YGuardRule(YGuardRule.TYPE_PACKAGE, pack);
      entries.add(rule);
    }
  }

  public void setMap(String mapTo) {
    this.mapTo = mapTo;
  }

  public void addMapEntries(Collection entries) {
    YGuardRule entry = new YGuardRule(YGuardRule.TYPE_PACKAGE_MAP, ObfuscatorTask.toNativeClass(name));
    entry.obfName = ObfuscatorTask.toNativeClass(mapTo);
    entries.add(entry);
  }

}
