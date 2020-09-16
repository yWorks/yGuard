package com.yworks.util.abstractjar;

import java.util.jar.JarEntry;

public class JarEntryWrapper implements Entry {
  JarEntry jarEntry;

  JarEntryWrapper( JarEntry jarEntry ) {
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
}
