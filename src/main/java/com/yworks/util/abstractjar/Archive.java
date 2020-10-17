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
  /**
   * Gets name.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets entries.
   *
   * @return the entries
   */
  Enumeration<Entry> getEntries();

  /**
   * Gets manifest.
   *
   * @return the manifest
   * @throws IOException the io exception
   */
  Manifest getManifest() throws IOException;

  /**
   * Gets input stream.
   *
   * @param entry the entry
   * @return the input stream
   * @throws IOException the io exception
   */
  InputStream getInputStream( Entry entry ) throws IOException;

  /**
   * Close.
   *
   * @throws IOException the io exception
   */
  void close() throws IOException;
}
