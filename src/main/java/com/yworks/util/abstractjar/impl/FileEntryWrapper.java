package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;

import java.io.File;
import java.nio.file.Path;

/**
 * The type File entry wrapper.
 */
public class FileEntryWrapper implements Entry {
  private File file;
  private String relative;

  /**
   * Instantiates a new File entry wrapper.
   *
   * @param relative relative path to the file
   * @param file the file
   */
  FileEntryWrapper( File file, String relative ) {
    this.relative = relative;
    this.file = file;
  }

  @Override
  public boolean isDirectory() {
    return file.isDirectory();
  }

  @Override
  public String getName() {
    return relative;
  }

  @Override
  public long getSize() {
    return file.length();
  }

  /**
   * Gets file.
   *
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * Creates an entry instance for the given file relative to the given directory.
   */
  static FileEntryWrapper newRelativeInstance( final File directory, final Path file ) {
    final String relative = directory.toPath().relativize(file).toString();
    return new FileEntryWrapper(file.toFile(), relative.replace(File.separatorChar, '/'));
  }
}
