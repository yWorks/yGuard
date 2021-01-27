package com.yworks.util.abstractjar;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Tests archive reader behavior.
 *
 * @author Thomas Behr
 */
public class ArchiveTest extends AbstractArchiveTest {
  public void testDirectoryReader() throws Exception {
    checkArchiveImpl(Utils.createTempDir("ArchiveTest"));
  }

  public void testJarReader() throws Exception {
    checkArchiveImpl(File.createTempFile("ArchiveTest", ".jar"));
  }

  @Override
  void checkArchiveCore( final File archive, final List<Entry> entries ) throws IOException {
    final Archive reader = Factory.newArchive(archive);
    final Manifest manifest = reader.getManifest();
    Assert.assertNotNull("Could not read manifest", manifest);

    for (Enumeration<Entry> en = reader.getEntries(); en.hasMoreElements();) {
      entries.add(en.nextElement());
    }
  }
}
