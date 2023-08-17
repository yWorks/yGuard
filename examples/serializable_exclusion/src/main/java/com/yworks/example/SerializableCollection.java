package com.yworks.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerializableCollection implements Iterable<Serializable>, Serializable {
  private static final long serialVersionUID = 5632652075812502119L;

  private transient List<Serializable> items;

  public SerializableCollection() {
    items = new ArrayList<Serializable>();
  }

  public void add( Serializable item ) {
    items.add(item);
  }

  @Override
  public Iterator<Serializable> iterator() {
    return items.iterator();
  }

  public void clear() {
    items.clear();
  }

  private void readObject( ObjectInputStream is ) throws ClassNotFoundException, IOException {
    if (items == null) {
      items = new ArrayList<Serializable>();
    } else {
      items.clear();
    }

    int size = is.readInt();
    for (int i = 0; i < size; ++i) {
      items.add((Serializable) is.readObject());
    }
  }

  private void writeObject( ObjectOutputStream os ) throws IOException {
    os.writeInt(items.size());
    for (Serializable item : items) {
      os.writeObject(item);
    }
  }
}
