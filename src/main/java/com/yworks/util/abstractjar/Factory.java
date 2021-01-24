package com.yworks.util.abstractjar;

import com.yworks.util.abstractjar.impl.DirectoryWriterImpl;
import com.yworks.util.abstractjar.impl.DirectoryStreamProvider;
import com.yworks.util.abstractjar.impl.DirectoryWrapper;
import com.yworks.util.abstractjar.impl.JarWriterImpl;
import com.yworks.util.abstractjar.impl.JarFileWrapper;
import com.yworks.util.abstractjar.impl.JarStreamProvider;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Creates facades for transparent usage of archive files,
 * that is jars and directories.
 *
 * @author Thomas Behr
 */
public class Factory {
  private Factory() {
  }

  public static Archive newArchive( final File archive ) throws IOException  {
    return archive.isDirectory()
      ? new DirectoryWrapper(archive)
      : new JarFileWrapper(archive);
  }

  public static StreamProvider newStreamProvider(
    final File archive
  ) throws IOException {
    return archive.isDirectory()
      ? new DirectoryStreamProvider(archive)
      : new JarStreamProvider(archive);
  }

  public static ArchiveWriter newArchiveWriter(
    final File archive, final Manifest manifest
  ) throws IOException {
    return archive.isDirectory()
      ? new DirectoryWriterImpl(archive, manifest)
      : new JarWriterImpl(archive, manifest);
  }
}
