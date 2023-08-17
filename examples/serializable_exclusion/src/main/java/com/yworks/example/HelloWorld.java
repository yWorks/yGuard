package com.yworks.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class HelloWorld {
  public static void main( String[] args ) {
    new HelloWorld().run();
  }

  private void run() {
    SerializableCollection source = new SerializableCollection();
    source.add(new SerializableItem(1, "Hello World"));
    source.add(new SerializableItem(2, "from serializable item"));
    dump("source", source);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    write(source, baos);

    SerializableCollection target = read(new ByteArrayInputStream(baos.toByteArray()));
    dump("target", target);
  }

  private void dump( String name, SerializableCollection data ) {
    final StringBuilder sb = new StringBuilder();
    sb.append("Items in ").append(name).append(":\n");
    for (Serializable item : data) {
      sb.append("  ").append(item);
    }
    System.out.println(sb.toString());
  }

  private SerializableCollection read( InputStream is ) {
    try {
      return (SerializableCollection) IoUtils.newObjectInputStream(is).readObject();
    } catch (Exception ex) {
      if (ex instanceof RuntimeException) {
        throw (RuntimeException) ex;
      } else {
        throw new RuntimeException(ex);
      }
    }
  }

  private void write( SerializableCollection data, OutputStream os ) {
    try {
      IoUtils.newObjectOutputStream(os).writeObject(data);
    } catch (Exception ex) {
      if (ex instanceof RuntimeException) {
        throw (RuntimeException) ex; 
      } else {
        throw new RuntimeException(ex);
      }
    }
  }
}