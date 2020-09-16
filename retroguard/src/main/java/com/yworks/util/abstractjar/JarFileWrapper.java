package com.yworks.util.abstractjar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarFileWrapper implements Archive {
  JarFile jarFile;
  Map<Entry, JarEntry> entries = new HashMap<>();

  public JarFileWrapper( File file ) throws IOException {
    jarFile = new JarFile(file);
    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
    while (jarEntryEnumeration.hasMoreElements()) {
      JarEntry jarEntry = jarEntryEnumeration.nextElement();
      entries.put(new JarEntryWrapper(jarEntry), jarEntry);
    }
  }

  @Override
  public String getName() {
    return jarFile.getName();
  }

  @Override
  public Enumeration<Entry> getEntries() {
    return Collections.enumeration(entries.keySet());
  }

  @Override
  public Manifest getManifest() throws IOException {
    return jarFile.getManifest();
  }

  @Override
  public InputStream getInputStream( final Entry entry ) throws IOException {
    return jarFile.getInputStream(entries.get(entry));
  }

  @Override
  public void close() throws IOException {
    jarFile.close();
  }
}
