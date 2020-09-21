package com.yworks.yshrink.core;

import com.yworks.yshrink.util.Util;
import com.yworks.yshrink.util.Version;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

class JarWriter {

  private Set<String> directoriesWritten = new HashSet<String>();
  private final FileOutputStream fos;
  private final JarOutputStream jos;
  private final Writer writer;

  private Manifest manifest;

  public JarWriter( File outFile, Manifest manifest, Writer writer ) throws IOException {

    this.manifest = (null != manifest) ? manifest : new Manifest();

    fos = new FileOutputStream(outFile);

    jos = new JarOutputStream(
            new BufferedOutputStream(
                    fos));
    this.writer = writer;
  }

  private void addDigests( String entryName ) {
    MessageDigest[] digests = writer.getDigests();
    Attributes oldEntryAttributes = this.manifest.getAttributes(entryName);
    Attributes newEntryAttributes = new Attributes(digests.length + 1);

    if (null != oldEntryAttributes) {
      Set<Object> keys = oldEntryAttributes.keySet();
      for (Object key : keys) {
        if (((Attributes.Name) key).toString().indexOf("Digest") == -1) {
          newEntryAttributes.put(key, oldEntryAttributes.get(key));
        }
      }
    }

    StringBuffer digestsList = new StringBuffer();
    for (int i = 0; i < digests.length; i++) {
      MessageDigest digest = digests[i];

      if (null != digest) {

        String digestKey = digest.getAlgorithm() + "-Digest";
        digestsList.append(digest.getAlgorithm());
        if (i < digests.length - 1) {
          digestsList.append(", ");
        }

        String digestVal = Util.toBase64(digest.digest());

        newEntryAttributes.putValue(digestKey, digestVal);
      }
    }

    newEntryAttributes.putValue("Digest-Algorithms", digestsList.toString());

    this.manifest.getEntries().put(entryName, newEntryAttributes);
  }

  public void addEntry( final String fileName, final byte[] data ) throws IOException {

    JarEntry outEntry = new JarEntry(fileName);
    addDirectory(fileName);
    jos.putNextEntry(outEntry);
    jos.write(data);
    jos.closeEntry();

    calcDigests(data);

    addDigests(fileName);
  }

  private void calcDigests( final byte[] data ) {
    MessageDigest[] digests = writer.getDigests();

    for (int i = digests.length - 1; i >= 0; i--) {
      if (null != digests[i]) {
        digests[i].reset();
        digests[i].update(data);
      }
    }
  }

  private void addDirectory( final String fileName ) throws IOException {
    int index = 0;
    while ((index = fileName.indexOf("/", index + 1)) >= 0) {
      String directory = fileName.substring(0, index + 1);
      if (!directoriesWritten.contains(directory)) {
        directoriesWritten.add(directory);
        JarEntry directoryEntry = new JarEntry(directory);
        jos.putNextEntry(directoryEntry);
        jos.closeEntry();
      }
    }
  }

  public void close() throws IOException {

    finishManifest();

    if (jos != null) {
      jos.finish();
      jos.close();
    }
    if (fos != null) {
      fos.close();
    }
  }

  private void finishManifest() throws IOException {
    manifest.getMainAttributes().putValue("Created-by",
                                          "yGuard Bytecode Obfuscator: Shrinker " + Version.getVersion());

    addDirectory(Writer.MANIFEST_FILENAME);
    jos.putNextEntry(new JarEntry(Writer.MANIFEST_FILENAME));
    this.manifest.write(jos);
    jos.closeEntry();
  }
}
