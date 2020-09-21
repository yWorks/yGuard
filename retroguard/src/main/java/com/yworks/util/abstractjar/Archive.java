package com.yworks.util.abstractjar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.Manifest;

/**
 * Describes an abstract "archive". This is usually a JAR archive.
 * However in Java terms using a directory to execute code (.class files) is perfectly fine.
 * Thus AbstractArchive provides an abstraction layer to account for both JAR files and directories (and potentially more).
 */
public interface Archive {
  String getName();

  Enumeration<Entry> getEntries();
  Manifest getManifest() throws IOException;
  InputStream getInputStream(Entry entry) throws IOException;
  void close() throws IOException;
}
