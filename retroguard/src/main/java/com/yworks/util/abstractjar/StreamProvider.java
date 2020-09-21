package com.yworks.util.abstractjar;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface StreamProvider {

  DataInputStream getNextClassEntryStream() throws IOException;

  DataInputStream getNextResourceEntryStream() throws IOException;

  Entry getCurrentEntry();

  String getCurrentEntryName();

  String getCurrentDir();

  String getCurrentFilename();

  void reset();
}
