package com.yworks.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Node {
  private final List<Edge> inEdges = new ArrayList<>();
  private final List<Edge> outEdges = new ArrayList<>();

  public Iterator getInEdges() {
    return inEdges.iterator();
  }

  public Iterator getOutEdges() {
    return outEdges.iterator();
  }

  public void addInEdge(Object edge) {
    inEdges.add((Edge) edge);
  }

  public void addOutEdge(Object edge) {
    outEdges.add((Edge) edge);
  }
}
