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
      final StringBuffer sb = new StringBuffer();

      final int[] tail = new int[1];
      final BufferedReader br = new BufferedReader(
              Streams.newTail(openReader(ignoreEncodingErrors), tail));
      try {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          sb.append(line).append('\n');
        }
      } finally {
        br.close();
      }

      if (sb.length() > 0 && tail[0] != '\n') {
        sb.setLength(sb.length() - 1);
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
