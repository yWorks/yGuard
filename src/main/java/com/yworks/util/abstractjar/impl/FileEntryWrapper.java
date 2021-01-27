package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;

import java.io.File;

/**
 * The type File entry wrapper.
 */
public class FileEntryWrapper implements Entry {
  private File file, directory;

  /**
   * Instantiates a new File entry wrapper.
   *
   * @param directory the top level directory
   * @param file the file
   */
  FileEntryWrapper( File directory, File file ) {
    this.directory = directory;
    this.file = file;
  }

  @Override
  public boolean isDirectory() {
    return file.isDirectory();
  }

  @Override
  public String getName() {
    return directory.toPath().relativize(file.toPath()).toString();
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
