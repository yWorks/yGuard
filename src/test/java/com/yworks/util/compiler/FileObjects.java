package com.yworks.util.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * Provides factory methods for {@link javax.tools.JavaFileObject} instances.
 *
 * @author Thomas Behr
 */
class FileObjects {
  private FileObjects() {
  }

  /**
   * New in memory file object java file object.
   *
   * @param typeName the type name
   * @param code     the code
   * @return the java file object
   */
  static JavaFileObject newInMemoryFileObject(
          final String typeName, final String code
  ) {
    return new InMemoryFileObject(typeName, code);
  }

  /**
   * New url file object java file object.
   *
   * @param typeName the type name
   * @param url      the url
   * @return the java file object
   */
  static JavaFileObject newUrlFileObject(
          final String typeName, final URL url
  ) {
    return new UrlFileObject(typeName, url);
  }



  private abstract static class AbstractSourceObject extends SimpleJavaFileObject {
    /**
     * Instantiates a new Abstract source object.
     *
     * @param typname the typname
     */
    AbstractSourceObject( final String typname) {
      super(asUri(typname), Kind.SOURCE);
    }

    /**
     * As uri uri.
     *
     * @param typeName the type name
     * @return the uri
     */
    static URI asUri( final String typeName ) {
      return URI.create("string:///" + typeName.replace('.', '/') + Kind.SOURCE.extension);
    }
  }

  private static final class InMemoryFileObject extends AbstractSourceObject {
    private final String code;

    /**
     * Instantiates a new In memory file object.
     *
     * @param typeName the type name
     * @param code     the code
     */
    InMemoryFileObject( final String typeName, final String code ) {
      super(typeName);
      this.code = code;
    }

    public CharSequence getCharContent(
            final boolean ignoreEncodingErrors
    ) throws IOException {
      return code;
    }
  }

  private static final class UrlFileObject extends AbstractSourceObject {
    private final URL url;

    /**
     * Instantiates a new Url file object.
     *
     * @param typname the typname
     * @param url     the url
     */
    UrlFileObject( final String typname, final URL url ) {
      super(typname);
      this.url = url;
    }

    public CharSequence getCharContent(
            final boolean ignoreEncodingErrors
    ) throws IOException {
      final StringBuilder sb = new StringBuilder();

      try (BufferedReader br = new BufferedReader(openReader(ignoreEncodingErrors))) {
        String del = "";
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          sb.append(del).append(line);
          del = "\n";
        }
      }

      return sb.toString();
    }

    public Reader openReader(
            final boolean ignoreEncodingErrors
    ) throws IOException {
      return new InputStreamReader(url.openStream(), "UTF-8");
    }
  }
}
