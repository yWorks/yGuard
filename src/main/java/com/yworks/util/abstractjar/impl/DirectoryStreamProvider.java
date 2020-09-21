package com.yworks.util.abstractjar.impl;

import com.yworks.util.abstractjar.Entry;
import com.yworks.util.abstractjar.StreamProvider;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DirectoryStreamProvider extends SimpleFileVisitor<Path> implements StreamProvider {
  private File directory;
  private List<Entry> entries = new ArrayList<>();
  private Iterator<Entry> entryIterator;
  private Entry currentEntry;

  public DirectoryStreamProvider( File directory ) throws IOException {
    this.directory = directory;
    Files.walkFileTree(directory.toPath(), this);
    entryIterator = entries.iterator();
  }

  @Override
  public FileVisitResult visitFile( final Path path, final BasicFileAttributes attrs ) throws IOException {
    if (attrs.isRegularFile()) {
      entries.add(new FileEntryWrapper(path.toFile()));
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public DataInputStream getNextClassEntryStream() throws IOException {
    FileEntryWrapper entry = null;

    while ( entryIterator.hasNext() ) {
      entry = (FileEntryWrapper)entryIterator.next();
      if ( entry.getName().endsWith(".class") ) {
        break;
      }
    }

    if ( entry != null  && entry.getName().endsWith(".class") ) {
      currentEntry = entry;
      return new DataInputStream( new BufferedInputStream(new FileInputStream(entry.getFile()) ));
    } else {
      currentEntry = null;
      return null;
    }
  }

  @Override
  public DataInputStream getNextResourceEntryStream() throws IOException {
    FileEntryWrapper entry = null;

    while ( entryIterator.hasNext() ) {
      entry = (FileEntryWrapper)entryIterator.next();
      if ( !entry.getName().endsWith(".class") ) {
        break;
      }
    }

    if ( entry != null  && !entry.getName().endsWith(".class") && !entry.isDirectory() ) {
      currentEntry = entry;
      return new DataInputStream( new BufferedInputStream(new FileInputStream(entry.getFile()) ));
    } else {
      currentEntry = null;
      return null;
    }
  }

  @Override
  public Entry getCurrentEntry() {
    return currentEntry;
  }

  @Override
  public String getCurrentEntryName() {
    if (currentEntry != null) {
      FileEntryWrapper entryWrapper = (FileEntryWrapper) currentEntry;
      return directory.toPath().relativize(entryWrapper.getFile().toPath()).toString().replace("\\", "/");
    }
    return null;
  }

  @Override
  public String getCurrentDir() {
    if (currentEntry != null) {
      FileEntryWrapper entryWrapper = (FileEntryWrapper) currentEntry;
      // NOTE: This is weird because apparently JAR files support \\ directory paths but not class paths. Mkay.
      return directory.toPath().relativize(entryWrapper.getFile().toPath().getParent()).toString();
    }
    return null;
  }

  @Override
  public String getCurrentFilename() {
    return (currentEntry != null) ? currentEntry.getName() : null;
  }

  @Override
  public void reset() {
    entryIterator = entries.iterator();
  }
}
