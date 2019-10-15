package com.yworks.yguard.obf;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;

import static junit.framework.TestCase.assertTrue;

/**
 * Provides utility methods for obfuscation tests.
 * @author Thomas Behr
 */
public class AbstractObfuscationTest {
  @Test
  public void satisfyTestRunner() {
    assertTrue("This is a volkswagen defeat device.", true);
  }

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

  static Compiler newCompiler() {
    try {
      final Class cmplrType = Class.forName("com.yworks.compiler.SimpleCompiler");
      final Object cmplrInst = cmplrType.newInstance();
      return new Compiler(cmplrType, cmplrInst);
    } catch (Exception ex) {
      if (ex instanceof RuntimeException) {
        throw (RuntimeException) ex;
      } else {
        throw new RuntimeException(ex);
      }
    }
  }

  static void write(
          final byte[] data, final File tgt
  ) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(tgt)) {
      fos.write(data);
      fos.flush();
    }
  }



  /**
   * Provides a simple facade for programmatic compilation of Java source code.
   * <p>
   * The facade calls the actual compiler implementation through reflection to
   * get exceptions instead of error if the compiler implementation cannot be
   * found.
   * </p>
   * @author Thomas Behr
   */
  static final class Compiler {
    private final Class cmplrType;
    private final Object cmplrInst;

    private Compiler( final Class cmplrType, final Object cmplrInst ) {
      this.cmplrType = cmplrType;
      this.cmplrInst = cmplrInst;
    }

    /**
     * Adds a compiler option.
     */
    public void addOption( final String option ) {
      try {
        final Method addOption = cmplrType.getMethod("addOption", String.class);
        addOption.invoke(cmplrInst, option);
      } catch (Exception ex) {
        if (ex instanceof RuntimeException) {
          throw (RuntimeException) ex;
        } else {
          throw new RuntimeException(ex);
        }
      }
    }

    /**
     * Creates source objects that can be compiled using method
     * {@link #compile(Iterable, OutputStream)}.
     */
    public Object newInMemorySource( final String typeName, final String code ) {
      try {
        final Method newSource = cmplrType.getMethod("newInMemorySource", String.class, String.class);
        return newSource.invoke(cmplrInst, typeName, code);
      } catch (Exception ex) {
        if (ex instanceof RuntimeException) {
          throw (RuntimeException) ex;
        } else {
          throw new RuntimeException(ex);
        }
      }
    }

    /**
     * Creates source objects that can be compiled using method
     * {@link #compile(Iterable, OutputStream)}.
     */
    public Object newUrlSource( final String typeName, final URL url ) {
      try {
        final Method newSource = cmplrType.getMethod("newUrlSource", String.class, URL.class);
        return newSource.invoke(cmplrInst, typeName, url);
      } catch (Exception ex) {
        if (ex instanceof RuntimeException) {
          throw (RuntimeException) ex;
        } else {
          throw new RuntimeException(ex);
        }
      }
    }

    /**
     * @param sources iterable of source objects created using method
     * {@link #newSource(String, String)}.
     * @param result a simple output stream. The compiled sources will
     * be written as java archive to this stream.
     */
    public boolean compile( final Iterable sources, final OutputStream result ) {
      try {
        final Method compile = cmplrType.getMethod("compile", Iterable.class, OutputStream.class);
        return ((Boolean) compile.invoke(cmplrInst, sources, result)).booleanValue();
      } catch (Exception ex) {
        if (ex instanceof RuntimeException) {
          throw (RuntimeException) ex;
        } else {
          throw new RuntimeException(ex);
        }
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
}
