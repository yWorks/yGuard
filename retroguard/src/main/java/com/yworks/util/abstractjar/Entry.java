package com.yworks.util.abstractjar;

/**
 * Entry represents an entry in an archive, e.g class file or resource file
 */
public interface Entry {
  boolean isDirectory();
  String getName();
  long getSize();
}
