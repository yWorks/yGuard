package com.yworks.util.compiler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides factory methods for streams
 *
 * @author Thomas Behr
 */
class Streams {
  private Streams() {
  }

  /**
   * New guard output stream.
   *
   * @param os the os
   * @return the output stream
   */
  static OutputStream newGuard( final OutputStream os ) {
    return new Guard(os);
  }


  private static final class Guard extends OutputStream {
    private final OutputStream os;

    /**
     * Instantiates a new Guard.
     *
     * @param os the os
     */
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
}
