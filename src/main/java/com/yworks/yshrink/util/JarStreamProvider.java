package com.yworks.yshrink.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class JarStreamProvider implements StreamProvider {

  private JarFile f;
  private Enumeration<? extends JarEntry> en;

  JarEntry currentEntry;
  private String currentEntryName;
  private String currentDir;
  private String currentFilename;

  public JarStreamProvider( final URL jarFile ) throws IOException {

    // TODO URL..

    if ( ! new File( jarFile.getFile() ).exists() ) {
      throw new IllegalArgumentException( "jar file not found: " + jarFile.getFile() );
    }
    f = new JarFile( jarFile.getFile() );
    en = f.entries();
  }

  public void reset() {
    en = f.entries();
  }

  public DataInputStream getNextStream() throws IOException {

    JarEntry entry = null;

    while ( en.hasMoreElements() ) {

      entry = en.nextElement();
      if ( !entry.isDirectory() ) {
        break;
      }
    }

    if ( entry != null ) { // && entry.getName().endsWith( ".class" )
      setCurrentEntry( entry );
      return new DataInputStream( new BufferedInputStream( f.getInputStream( entry ) ) );
    } else {
      setCurrentEntry( null );
      return null;
    }
  }

  public DataInputStream getNextClassEntryStream() throws IOException {

    JarEntry entry = null;

    while ( en.hasMoreElements() ) {

      entry = en.nextElement();
      if ( entry.getName().endsWith( ".class" ) ) {
        break;
      }
    }

    if ( entry != null && entry.getName().endsWith( ".class" ) ) {
      setCurrentEntry( entry );
      return new DataInputStream( new BufferedInputStream( f.getInputStream( entry ) ) );
    } else {
      setCurrentEntry( null );
      return null;
    }
  }

  public DataInputStream getNextResourceEntryStream() throws IOException {
    JarEntry entry = null;

    while ( en.hasMoreElements() ) {

      entry = en.nextElement();
      if ( !entry.getName().endsWith( ".class" ) && ! entry.isDirectory() ) {
        break;
      }
    }

    if ( entry != null && !entry.getName().endsWith( ".class" ) && ! entry.isDirectory() ) {
      setCurrentEntry( entry );
      return new DataInputStream( new BufferedInputStream( f.getInputStream( entry ) ) );
    } else {
      setCurrentEntry( null );
      return null;
    }
  }

  public JarEntry currentEntry() {
    return currentEntry;
  }

  public String getCurrentDir() {
    return currentDir;
  }

  public String getCurrentEntryName() {
    return currentEntryName;
  }

  public String getCurrentFilename() {
    return currentFilename;
  }

  private void setCurrentEntry( JarEntry entry ) {

    if ( null != entry ) {
      currentEntry = entry;
      currentEntryName = currentEntry.getName();
      File entryFile = new File( currentEntryName );
      currentDir = ( entryFile.getParent() ) != null ? entryFile.getParent() : "";
      currentFilename = entryFile.getName();
    } else {
      currentEntry = null;
      currentDir = null;
      currentFilename = null;
    }
  }
}
