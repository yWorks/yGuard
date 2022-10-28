package com.yworks.yguard.obf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.yworks.util.Compiler;
import com.yworks.util.InMemoryArchive;
import com.yworks.util.abstractjar.Archive;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Provides utility methods for obfuscation tests.
 *
 * @author Thomas Behr
 */
public class AbstractObfuscationTest {
  /**
   * Satisfy test runner.
   */
  @Test
  public void satisfyTestRunner() {
    assertTrue("This is a volkswagen defeat device.", true);
  }

  Archive newArchive(
    final String name,
    final Iterable<TypeStruct> sourceFiles,
    final Iterable<EntryStruct> otherFiles
  ) throws IOException {
    return newArchiveImpl(name, sourceFiles, otherFiles);
  }

  private Archive newArchiveImpl(
   final String name,
   final Iterable<TypeStruct> sourceFiles,
   final Iterable<EntryStruct> otherFiles
  ) throws IOException {
    final Class<?> resolver = getClass();

    final Compiler compiler = Compiler.newCompiler();

    final ArrayList<Object> sources = new ArrayList<Object>();
    for (TypeStruct struct : sourceFiles) {
      final URL url = resolver.getResource(struct.fileName);
      assertNotNull("Could not resolve " + struct.fileName + '.', url);

      sources.add(compiler.newUrlSource(struct.typeName, url));
    }

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    compiler.compile(sources, baos);

    final InMemoryArchive archive = new InMemoryArchive(name);
    archive.addAll(baos.toByteArray());

    for (EntryStruct struct : otherFiles) {
      if (struct.fileName == null) {
        archive.add(struct.entryName, new ByteArrayInputStream(new byte[0]));
      } else {
        final URL url = resolver.getResource(struct.fileName);
        assertNotNull("Could not resolve " + struct.fileName + '.', url);

        try (InputStream is = url.openStream()) {
          archive.add(struct.entryName, is);
        }
      }
    }

    archive.freeze();
    return archive;
  }

  /**
   * Gets major version.
   *
   * @return the major version
   */
  static int getMajorVersion() {
    final String v = System.getProperty("java.version");
    if (v == null) {
      return 1;
    } else {
      final int offset = v.startsWith("1.") ? 2 : 0;
      final int idx = v.indexOf('.', offset);
      if (idx > -1) {
        return Integer.parseInt(v.substring(offset, idx));
      } else {
        return Integer.parseInt(v.substring(offset));
      }
    }
  }

  /**
   * Write.
   *
   * @param data the data
   * @param tgt  the tgt
   * @throws IOException the io exception
   */
  static void write(
          final byte[] data, final File tgt
  ) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(tgt)) {
      fos.write(data);
      fos.flush();
    }
  }

//  private static Method getMethod(
//          final Class<?> c, final String methodName
//  ) throws NoSuchMethodException {
//    final ArrayList<Method> matches = new ArrayList<>();
//    for (Method method : c.getMethods()) {
//      if (methodName.equals(method.getName())) {
//        matches.add(method);
//      }
//    }
//    if (matches.isEmpty()) {
//      throw new NoSuchMethodException(methodName);
//    } else {
//      Collections.sort(matches, new MethodComparator());
//      return matches.get(0);
//    }
//  }
//
//
//  private static final class MethodComparator implements Comparator<Method> {
//    public int compare( final Method o1, final Method o2 ) {
//      final int pc1 = o1.getParameterTypes().length;
//      final int pc2 = o1.getParameterTypes().length;
//      return Integer.compare(pc1, pc2);
//    }
//  }


  static final class EntryStruct {
    final String fileName;
    final String entryName;

    EntryStruct( final String fileName, final String entryName ) {
      this.fileName = fileName;
      this.entryName = entryName;
    }
  }

  static final class TypeStruct {
    final String fileName;
    final String typeName;

    TypeStruct( final String fileName, final String typeName ) {
      this.fileName = fileName;
      this.typeName = typeName;
    }
  }
}
