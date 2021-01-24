package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.ArchiveWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Writes contents to jar files.
 *
 * @author Thomas Behr
 */
public class JarWriterImpl extends ArchiveWriter {
  private JarOutputStream jos;

  public JarWriterImpl( final File archive, final Manifest man ) throws IOException {
    super(man);

    jos = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(archive)), man);
  }

  @Override
  public void setComment( final String comment ) {
    jos.setComment(comment);
  }

  @Override
  public void addDirectory( final String path ) throws IOException {
    ensureValid(path);
    newEntry(jos, ensureDirName(path));
    jos.closeEntry();
  }

  @Override
  public void addFile( final String path, final byte[] data ) throws IOException {
    ensureValid(path);
    newEntry(jos, path);
    jos.write(data);
    jos.closeEntry();
  }

  @Override
  public void close() throws IOException {
    if (jos != null) {
      jos.flush();
      jos.close();
      jos = null;
    }
  }


  private static void newEntry(
    final JarOutputStream jos, final String path
  ) throws IOException {
    jos.putNextEntry(new JarEntry(path));
  }

  private static String ensureDirName( final String path ) {
    return path.endsWith("/") ? path : path + '/';
  }

  private static void ensureValid( final String path ) {
    if (path == null) {
      throw new NullPointerException("path");
    }
  }
}
