package com.yworks.yguard.obf;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
