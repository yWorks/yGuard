package com.yworks.util.compiler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Provides factory methods for streams
 * @author Thomas Behr
 */
class Streams {
  private Streams() {
  }

  static OutputStream newGuard( final OutputStream os ) {
    return new Guard(os);
  }

  static Reader newTail( final Reader r, final int[] tail ) {
    return new Tail(r, tail);
  }


  private static final class Guard extends OutputStream {
    private final OutputStream os;

    Guard( final OutputStream os ) {
      this.os = os;
    }

    public void write( final int b ) throws IOException {
      os.write(b);
    }

    public void write( final byte[] b ) throws IOException {
      os.write(b);
    }

    public void write( final byte[] b, final int off, final int len ) throws IOException {
      os.write(b, off, len);
    }

    public void flush() throws IOException {
      os.flush();
    }

    public void close() throws IOException {
      // do not close - this is the whole point of the guard
      os.flush();
    }
  }

  private static final class Tail extends Reader {
    private final Reader r;
    private final int[] tail;

    Tail( final Reader r, final int[] tail ) {
      this.r = r;
      this.tail = tail;
    }

    public int read( final CharBuffer target ) throws IOException {
      final int read = r.read(target);
      if (read > -1) {
        tail[0] = target.charAt(read - 1);
      }
      return read;
    }

    public int read() throws IOException {
      return tail[0] = r.read();
    }

    public int read( final char[] cbuf ) throws IOException {
      final int read = r.read(cbuf);
      if (read > -1) {
        tail[0] = cbuf[read - 1];
      }
      return read;
    }

    public int read( final char[] cbuf, final int off, final int len ) throws IOException {
      final int read = r.read(cbuf, off, len);
      if (read > -1) {
        tail[0] = cbuf[off + read - 1];
      }
      return read;
    }

    public long skip( final long n ) throws IOException {
      return r.skip(n);
    }

    public boolean ready() throws IOException {
      return r.ready();
    }

    public boolean markSupported() {
      return r.markSupported();
    }

    public void mark( final int readAheadLimit ) throws IOException {
      r.mark(readAheadLimit);
    }

    public void reset() throws IOException {
      r.reset();
    }

    public void close() throws IOException {
      r.close();
    }
  }
}
