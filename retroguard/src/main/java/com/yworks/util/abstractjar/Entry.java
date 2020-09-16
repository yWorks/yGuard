package com.yworks.util.abstractjar;

/**
 * Entry represents an entry in an archive, e.g class file or resource file
 */
public interface Entry {
  public boolean isDirectory();
  public String getName();
  public long getSize();
}
