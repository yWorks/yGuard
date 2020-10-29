package com.yworks.util.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Package-private class used by DefaultNetwork to represent an Node.
 */
class Node {
  private final List<Edge> inEdges = new ArrayList<>();
  private final List<Edge> outEdges = new ArrayList<>();

  public List<Edge> getInEdges() {
    return inEdges;
  }

  public List<Edge> getOutEdges() {
    return outEdges;
  }

  public void addInEdge(Object edge) {
    inEdges.add((Edge) edge);
  }

  public void addOutEdge(Object edge) {
    outEdges.add((Edge) edge);
  }
}
