package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;

import java.io.File;

public class FileEntryWrapper implements Entry {
  private File file;

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

  public File getFile() {
    return file;
  }
}
