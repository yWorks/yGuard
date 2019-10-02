package test.simple;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ObjectInputStream;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
class SerialA implements Serializable {

  public SerialA() {}

  private void writeObject( ObjectOutputStream s ) throws IOException {
    s.defaultWriteObject();
    // customized serialization code
  }

  private void readObject( ObjectInputStream s ) throws IOException {
    try {
      s.defaultReadObject();
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace(); //TODO handle
    }
  }
}
