package com.yworks.util.abstractjar;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * The interface Stream provider.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface StreamProvider {

  /**
   * Gets next class entry stream.
   *
   * @return the next class entry stream
   * @throws IOException the io exception
   */
  DataInputStream getNextClassEntryStream() throws IOException;

  /**
   * Gets next resource entry stream.
   *
   * @return the next resource entry stream
   * @throws IOException the io exception
   */
  DataInputStream getNextResourceEntryStream() throws IOException;

  /**
   * Gets current entry.
   *
   * @return the current entry
   */
  Entry getCurrentEntry();

  /**
   * Gets current entry name.
   *
   * @return the current entry name
   */
  String getCurrentEntryName();

  /**
   * Gets current dir.
   *
   * @return the current dir
   */
  String getCurrentDir();

  /**
   * Gets current filename.
   *
   * @return the current filename
   */
  String getCurrentFilename();

  /**
   * Resets the stream provider.
   */
  void reset();

  /**
   * Closes the stream provider.
   */
  void close() throws IOException;
}
