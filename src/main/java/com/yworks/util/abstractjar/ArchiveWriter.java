package com.yworks.util.abstractjar;

import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Specifies the contract for adding content to archive files,
 * that is jars and directories.
 *
 * @author Thomas Behr
 */
public abstract class ArchiveWriter {
  protected ArchiveWriter( Manifest man ) throws IOException {
    if (man == null) {
      throw new NullPointerException("man");
    }
  }

  public abstract void setComment( String comment );

  public abstract void addDirectory( String path ) throws IOException;

  public abstract void addFile( String path, byte[] data ) throws IOException;

  public abstract void close() throws IOException;
}
