package com.yworks.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

class IoUtils {
  private IoUtils() {
  }

  static ObjectInputStream newObjectInputStream( InputStream is ) throws IOException {
    if (is instanceof ObjectInputStream) {
      return (ObjectInputStream) is;
    } else {
      return new ObjectInputStream(new MyInputStream(is));
    }
  }

  static ObjectOutputStream newObjectOutputStream( OutputStream os ) throws IOException {
    if (os instanceof ObjectOutputStream) {
      return (ObjectOutputStream) os;
    } else {
      return new ObjectOutputStream(new MyOutputStream(os));
    }
  }



  private static final class MyInputStream extends InputStream {
    private final InputStream is;

    MyInputStream( InputStream is ) {
      this.is = is;
    }

    @Override
    public int read() throws IOException {
      return is.read();
    }

    @Override
    public int read( final byte[] b ) throws IOException {
      return is.read(b);
    }

    @Override
    public int read( final byte[] b, final int off, final int len ) throws IOException {
      return is.read(b, off, len);
    }

    @Override
    public long skip( final long n ) throws IOException {
      return is.skip(n);
    }

    @Override
    public int available() throws IOException {
      return is.available();
    }

    @Override
    public void close() throws IOException {
      // do not close wrapped stream
    }

    @Override
    public void mark( final int readlimit ) {
      is.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
      is.reset();
    }

    @Override
    public boolean markSupported() {
      return is.markSupported();
    }
  }

  private static final class MyOutputStream extends OutputStream {
    private final OutputStream os;

    MyOutputStream( OutputStream os ) {
      this.os = os;
    }

    @Override
    public void write( final int b ) throws IOException {
      os.write(b);
    }

    @Override
    public void write( final byte[] b ) throws IOException {
      os.write(b);
    }

    @Override
    public void write( final byte[] b, final int off, final int len ) throws IOException {
      os.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
      os.flush();
    }

    @Override
    public void close() throws IOException {
      // do not close wrapped stream
    }
  }
}
