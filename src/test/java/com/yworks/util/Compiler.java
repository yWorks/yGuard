package com.yworks.util;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Provides a simple facade for programmatic compilation of Java source code.
 * <p>
 * The facade calls the actual compiler implementation through reflection to
 * get exceptions instead of error if the compiler implementation cannot be
 * found.
 * </p>
 * @author Thomas Behr
 */
public final class Compiler {
  private final Class cmplrType;
  private final Object cmplrInst;

  /**
   * Initializes a new <code>Compiler</code> instance.
   */
  public Compiler( final Class cmplrType, final Object cmplrInst ) {
    this.cmplrType = cmplrType;
    this.cmplrInst = cmplrInst;
  }

  /**
   * Creates a new <code>Compiler</code> instance.
   */
  public static Compiler newCompiler() {
    try {
      final Class cmplrType = Class.forName("com.yworks.util.compiler.SimpleCompiler");
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
   * @param result  a simple output stream. The compiled sources will
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
}
