package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;

import java.io.File;

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
}
