package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;

import java.io.File;

/**
 * The type File entry wrapper.
 */
public class FileEntryWrapper implements Entry {
  private final File file;

  /**
   * Instantiates a new File entry wrapper.
   *
   * @param file the file
   */
  FileEntryWrapper( File file ) {
    this.file = file;
  }

  @Override
  public boolean isDirectory() {
    return file.isDirectory();
  }

  @Override
  public String getName() {
    return file.getName();
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
