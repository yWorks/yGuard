package com.yworks.util;

import com.yworks.util.abstractjar.Archive;
import com.yworks.util.abstractjar.Entry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Stores the byte-code for entries in-memory to prevent the need for
 * creating temporary files when testing yGuard's obfuscation features.
 *
 * @author Thomas Behr
 */
public class InMemoryArchive implements Archive {
  private final String name;
  private final List<Entry> entries;
  private byte[] data;

  private ByteArrayOutputStream baos;

  public InMemoryArchive( final String name ) {
    this.name = name;
    this.entries = new ArrayList<Entry>(1);
    this.data = new byte[8 * 1024 * 1024];
    this.baos = new ByteArrayOutputStream();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Enumeration<Entry> getEntries() {
    checkReadable();
    return new IterableAdapter<Entry>(entries);
  }

  @Override
  public Manifest getManifest() throws IOException {
    return null;
  }

  @Override
  public InputStream getInputStream( final Entry entry ) throws IOException {
    checkReadable();
    final OffsetEntry e = (OffsetEntry) entry;
    return new ByteArrayInputStream(data, e.offset, e.size);
  }

  @Override
  public void close() throws IOException {
  }

  public void freeze() {
    data = baos.toByteArray();
    baos = null;
  }

  public void addAll( final byte[] data ) throws IOException {
    checkWritable();

    final JarInputStream jis = new JarInputStream(new ByteArrayInputStream(data));
    try {
      addAllImpl(jis);
    } finally {
      close(jis);
    }
  }

//  void addAll( final JarInputStream jis ) throws IOException {
//    checkWritable();
//    addAllImpl(jis);
//  }

  public void add( final String name, final byte[] data ) throws IOException {
    checkWritable();

    final ByteArrayInputStream bais = new ByteArrayInputStream(data);
    try {
      addImpl(false, name, bais);
    } finally {
      close(bais);
    }
  }

  public void add( final String name, final InputStream is ) throws IOException {
    checkWritable();
    addImpl(false, name, is);
  }

  private void addAllImpl( final JarInputStream jis ) throws IOException {
    for (JarEntry entry = jis.getNextJarEntry(); entry != null; entry = jis.getNextJarEntry()) {
      addImpl(entry.isDirectory(), entry.getName(), jis);
    }
  }

  private void addImpl(
    final boolean dir, final String name, final InputStream is
  ) throws IOException {
    final int offset = baos.size();

    int size = 0;
    for (int read = is.read(data); read > -1; read = is.read(data)) {
      size += read;
      baos.write(data, 0, read);
    }

    entries.add(new OffsetEntry(dir, name, size, offset));
  }

  private void checkReadable() {
    if (baos != null) {
      throw new IllegalStateException();
    }
  }

  private void checkWritable() {
    if (baos == null) {
      throw new IllegalStateException();
    }
  }

  private static void close( final InputStream is ) {
    try {
      is.close();
    } catch (Exception ex) {
      // ignore
    }
  }


  private static final class IterableAdapter<T> implements Enumeration<T> {
    private final Iterator<T> it;

    IterableAdapter( final Iterable<T> iterable ) {
      it = iterable.iterator();
    }

    @Override
    public boolean hasMoreElements() {
      return it.hasNext();
    }

    @Override
    public T nextElement() {
      return it.next();
    }
  }

  private static class OffsetEntry implements Entry {
    final boolean dir;
    final String name;
    final int size;
    final int offset;

    OffsetEntry(
      final boolean dir, final String name, final int size, final int offset
    ) {
      this.dir = dir;
      this.name = name;
      this.size = size;
      this.offset = offset;
    }

    @Override
    public boolean isDirectory() {
      return dir;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public long getSize() {
      return size;
    }
  }
}
