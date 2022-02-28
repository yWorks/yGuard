package com.yworks.util.abstractjar;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tests stream provider behavior.
 *
 * @author Thomas Behr
 */
public class StreamProviderTest extends AbstractArchiveTest {
  public void testDirectoryReader() throws Exception {
    checkArchiveImpl(Utils.createTempDir("ArchiveTest"));
  }

  public void testJarReader() throws Exception {
    checkArchiveImpl(File.createTempFile("ArchiveTest", ".jar"));
  }


  @Override
  void checkArchiveCore(
    final File archive, final List<Entry> entries
  ) throws IOException {
    final StreamProvider provider = Factory.newStreamProvider(archive);

    for (DataInputStream stream = provider.getNextClassEntryStream();
         stream != null;
         stream = provider.getNextClassEntryStream()) {
      entries.add(provider.getCurrentEntry());
      stream.close();
    }

    provider.reset();
    for (DataInputStream stream = provider.getNextResourceEntryStream();
         stream != null;
         stream = provider.getNextResourceEntryStream()) {
      entries.add(provider.getCurrentEntry());
      stream.close();
    }

    provider.close();
  }
}
