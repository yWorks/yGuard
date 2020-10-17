package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;

import java.util.jar.JarEntry;

/**
 * The type Jar entry wrapper.
 */
public class JarEntryWrapper implements Entry {
  /**
   * The Jar entry.
   */
  JarEntry jarEntry;

  /**
   * Instantiates a new Jar entry wrapper.
   *
   * @param jarEntry the jar entry
   */
  public JarEntryWrapper( JarEntry jarEntry ) {
    this.jarEntry = jarEntry;
  }

  @Override
  public boolean isDirectory() {
    return jarEntry.isDirectory();
  }

  @Override
  public String getName() {
    return jarEntry.getName();
  }

  @Override
  public long getSize() {
    return jarEntry.getSize();
  }

  /**
   * Gets jar entry.
   *
   * @return the jar entry
   */
  public JarEntry getJarEntry() {
    return jarEntry;
  }
}
