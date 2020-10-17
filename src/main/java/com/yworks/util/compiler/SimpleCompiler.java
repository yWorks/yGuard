package com.yworks.util.compiler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 * Compiles Java source code directly to a JAR stream.
 *
 * @author Thomas Behr
 */
public class SimpleCompiler {
  private List options;

  /**
   * Adds a compiler option.
   *
   * @param option the option
   */
  public void addOption( final String option ) {
    if (options == null) {
      options = new ArrayList();
    }
    options.add(option);
  }

  /**
   * Creates source objects that can be compiled using method
   * {@link #compile(Iterable, OutputStream)}.
   *
   * @param typeName the type name
   * @param code     the code
   * @return the object
   */
  public Object newInMemorySource( final String typeName, final String code ) {
    return FileObjects.newInMemoryFileObject(typeName, code);
  }

  /**
   * Creates source objects that can be compiled using method
   * {@link #compile(Iterable, OutputStream)}.
   *
   * @param typeName the type name
   * @param url      the url
   * @return the object
   */
  public Object newUrlSource( final String typeName, final URL url ) {
    return FileObjects.newUrlFileObject(typeName, url);
  }

  /**
   * Compile boolean.
   *
   * @param sources iterable of source objects created using method
   * @param result  a simple output stream. The compiled sources will be written as java archive to this stream.
   * @return the boolean
   */
  public boolean compile( final Iterable sources, final OutputStream result ) {
    try {
      return compileImpl(sources, result);
    } catch (IOException ioe) {
      return false;
    }
  }


  private boolean compileImpl(
          final Iterable sources, final OutputStream result
  ) throws IOException {
    final JarOutputStream jos = new JarOutputStream(Streams.newGuard(result));
    try {
      return compileCore(sources, jos);
    } finally {
      jos.close();
    }
  }

  private boolean compileCore( final Iterable sources, final JarOutputStream jos ) {
    final StringWriter compilerOut = new StringWriter();
    final DiagnosticCollector dc = new DiagnosticCollector();

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final JavaCompiler.CompilationTask task = compiler.getTask(
            compilerOut,
            new InMemoryFileManager(jos, compiler, dc),
            dc,
            options,
            null,
            sources);
    final Boolean result = task.call();
    return result.booleanValue();
  }


  private static final class InMemoryFileManager extends ForwardingJavaFileManager {
    private JarOutputStream jos;
    private boolean hasEntry;

    /**
     * Instantiates a new In memory file manager.
     *
     * @param jos      the jos
     * @param compiler the compiler
     * @param dc       the dc
     */
    InMemoryFileManager(
            final JarOutputStream jos,
            final JavaCompiler compiler,
            final DiagnosticCollector dc
    ) {
      super(compiler.getStandardFileManager(dc, Locale.US, StandardCharsets.UTF_8));
      this.jos = jos;
    }

    public JavaFileObject getJavaFileForOutput(
            final Location location,
            final String className,
            final JavaFileObject.Kind kind,
            final FileObject sibling
    ) throws IOException {
      jos.putNextEntry(new JarEntry(className.replace('.', '/') + JavaFileObject.Kind.CLASS.extension));
      return new StreamFileObject(jos, super.getJavaFileForOutput(location, className, kind, sibling));
    }

    public void flush() throws IOException {
      super.flush();
      jos.flush();
    }

    public void close() throws IOException {
      super.close();
      jos.close();
      jos = null;
    }
  }

  private static class StreamFileObject extends ForwardingJavaFileObject {
    private final JarOutputStream jos;

    /**
     * Instantiates a new Stream file object.
     *
     * @param jos the jos
     * @param jfo the jfo
     */
    StreamFileObject( final JarOutputStream jos, final JavaFileObject jfo ) {
      super(jfo);
      this.jos = jos;
    }

    public OutputStream openOutputStream() throws IOException {
      return Streams.newGuard(jos);
    }
  }
}
