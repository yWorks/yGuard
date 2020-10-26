package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Archive;
import com.yworks.util.abstractjar.Entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * The type Directory wrapper.
 */
public class DirectoryWrapper extends SimpleFileVisitor<Path> implements Archive {
  private File directory;
  private Map<Entry, File> entries = new HashMap<>();

    /**
     * Instantiates a new Directory wrapper.
     *
     * 
		 * @param directory the directory
     * @throws IOException the io exception
     */
    public DirectoryWrapper( File directory ) throws IOException {
    this.directory = directory;
    Files.walkFileTree(directory.toPath(), this);
  }

  @Override
  public FileVisitResult visitFile( final Path path, final BasicFileAttributes attrs ) throws IOException {
    if ( attrs.isRegularFile() ) {
      entries.put(new FileEntryWrapper(path.toFile()), path.toFile());
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public String getName() {
    return directory.getName();
  }

  @Override
  public Enumeration<Entry> getEntries() {
    return Collections.enumeration(entries.keySet());
  }

  @Override
  public Manifest getManifest() throws IOException {
    return null;
  }

  @Override
  public InputStream getInputStream( final Entry entry ) throws IOException {
    return new FileInputStream(entries.get(entry));
  }

  @Override
  public void close() throws IOException {
    // NOTE: Legacy. Do nothing here.
  }
}
