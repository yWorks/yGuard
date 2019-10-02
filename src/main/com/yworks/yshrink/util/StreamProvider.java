package com.yworks.yshrink.util;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public interface StreamProvider {

  public DataInputStream getNextStream() throws IOException;

  DataInputStream getNextClassEntryStream() throws IOException;

  DataInputStream getNextResourceEntryStream() throws IOException;

}
