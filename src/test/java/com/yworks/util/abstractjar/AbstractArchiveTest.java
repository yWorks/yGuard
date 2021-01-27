package com.yworks.util.abstractjar;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Tests stream provider behavior.
 *
 * @author Thomas Behr
 */
abstract class AbstractArchiveTest extends TestCase {
  void checkArchiveImpl( final File archive ) throws Exception {
    try {
      final String tempClassName = getClass().getName();
      final String tempFileName = tempClassName.replace('.', '/') + ".class";


      prepareArchive(archive, tempClassName, tempFileName);


      final ArrayList<Entry> entries = new ArrayList<>();
      checkArchiveCore(archive, entries);

      Assert.assertEquals(
        "Wrong entry count",
        2,
        entries.size());

      Collections.sort(entries, new EntryComparator());

      Assert.assertEquals(
        "Wrong entry name",
        JarFile.MANIFEST_NAME,
        entries.get(0).getName());
      Assert.assertEquals(
        "Wrong entry name",
        tempFileName,
        entries.get(1).getName());
    } finally {

      Utils.delete(archive);
    }
  }

  abstract void checkArchiveCore(
    File archive, List<Entry> entries
  ) throws IOException;


  private static void prepareArchive(
          final File archive,
          final String tempClassName,
          final String tempFileName
  ) throws IOException {
    final Manifest manifest = new Manifest();
    final Attributes attributes = manifest.getMainAttributes();
    attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    attributes.put(Attributes.Name.MAIN_CLASS, tempClassName);

    final ArchiveWriter writer = Factory.newArchiveWriter(archive, manifest);
    writer.addFile(tempFileName, new byte[0]);
    writer.close();
  }



  private static class EntryComparator implements Comparator<Entry> {
    @Override
    public int compare( final Entry o1, final Entry o2 ) {
      final String n1 = o1.getName();
      final String n2 = o2.getName();
      return n1.compareTo(n2);
    }
  }
}
