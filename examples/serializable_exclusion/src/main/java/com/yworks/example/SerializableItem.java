package com.yworks.example;

import java.io.Serializable;

public class SerializableItem implements Serializable {
  private static final long serialVersionUID = -9206976987376375747L;

  private int rank;
  private String description;

  public SerializableItem( final int rank, final String description ) {
    this.rank = rank;
    this.description = description;
  }

  @Override
  public String toString() {
    return rank + ": " + description;
  }
}
