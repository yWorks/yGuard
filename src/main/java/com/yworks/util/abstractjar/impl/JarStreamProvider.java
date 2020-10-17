package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;
import com.yworks.util.abstractjar.StreamProvider;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The type Jar stream provider.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class JarStreamProvider implements StreamProvider {

  private final JarFile f;
  private Enumeration<? extends JarEntry> en;

  /**
   * The Current entry.
   */
  JarEntry currentEntry;
  private String currentEntryName;
  private String currentDir;
  private String currentFilename;

  /**
   * Instantiates a new Jar stream provider.
   *
   * @param jarFile the jar file
   * @throws IOException the io exception
   */
  public JarStreamProvider( final File jarFile ) throws IOException {
    if (!jarFile.exists()) {
      throw new IllegalArgumentException("jar file not found: " + jarFile.toString());
    }

    f = new JarFile(jarFile);
    en = f.entries();
  }

  @Override
  public void reset() {
    en = f.entries();
  }

  @Override
  public DataInputStream getNextClassEntryStream() throws IOException {

    JarEntry entry = null;

    while (en.hasMoreElements()) {

      entry = en.nextElement();
      if (entry.getName().endsWith(".class")) {
        break;
      }
    }

    if (entry != null && entry.getName().endsWith(".class")) {
      setCurrentEntry(entry);
      return new DataInputStream(new BufferedInputStream(f.getInputStream(entry)));
    } else {
      setCurrentEntry(null);
      return null;
    }
  }

  @Override
  public DataInputStream getNextResourceEntryStream() throws IOException {
    JarEntry entry = null;

    while (en.hasMoreElements()) {

      entry = en.nextElement();
      if (!entry.getName().endsWith(".class") && !entry.isDirectory()) {
        break;
      }
    }

    if (entry != null && !entry.getName().endsWith(".class") && !entry.isDirectory()) {
      setCurrentEntry(entry);
      return new DataInputStream(new BufferedInputStream(f.getInputStream(entry)));
    } else {
      setCurrentEntry(null);
      return null;
    }
  }

  @Override
  public Entry getCurrentEntry() {
    return new JarEntryWrapper(currentEntry);
  }

  @Override
  public String getCurrentEntryName() {
    return currentEntryName;
  }

  public String getCurrentDir() {
    return currentDir;
  }

  public String getCurrentFilename() {
    return currentFilename;
  }

  private void setCurrentEntry( JarEntry entry ) {

    if (null != entry) {
      currentEntry = entry;
      currentEntryName = currentEntry.getName();
      File entryFile = new File(currentEntryName);
      currentDir = (entryFile.getParent()) != null ? entryFile.getParent() : "";
      currentFilename = entryFile.getName();
    } else {
      currentEntry = null;
      currentDir = null;
      currentFilename = null;
    }
  }
}
