package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.ArchiveWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Writes content to directories.
 *
 * @author Thomas Behr
 */
public class DirectoryWriterImpl extends ArchiveWriter {
  private final File archive;

  public DirectoryWriterImpl(
    final File archive, final Manifest man
  ) throws IOException {
    super(man);

    this.archive = archive;

    writeManifest(man);
  }

  @Override
  public void setComment( final String comment ) {
    // do nothing
  }

  @Override
  public void addDirectory( final String path ) throws IOException {
    final File file = new File(archive, path);
    makeDirs(file);
  }

  @Override
  public void addFile( final String path, final byte[] data ) throws IOException {
    final File tgt = new File(archive, path);
    ensurePath(tgt);
    final OutputStream os = new FileOutputStream(tgt);
    try {
      os.write(data);
      os.flush();
    } finally {
      os.close();
    }
  }

  @Override
  public void close() throws IOException {
    // do nothing
  }


  private void writeManifest( final Manifest manifest ) throws IOException{
    final File tgt = new File(archive, JarFile.MANIFEST_NAME);
    ensurePath(tgt);
    final OutputStream os = new BufferedOutputStream(new FileOutputStream(tgt));
    try {
      manifest.write(os);
      os.flush();
    } finally {
      os.close();
    }
  }

  private static void ensurePath( final File file ) throws IOException {
    makeDirs(file.getParentFile());
  }

  private static void makeDirs( final File file ) throws IOException {
    if (!file.isDirectory()) {
      if (!file.mkdirs()) {
        throw new IOException("Could not create directory " + file.getAbsolutePath() + '.');
      }
    }
  }
}
