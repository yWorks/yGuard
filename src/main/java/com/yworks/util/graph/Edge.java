package com.yworks.util.graph;

/**
 * Package-private class used by DefaultNetwork to represent an Edge.
 */
class Edge {
  private final Node source;
  private final Node target;

  public Edge(Node src, Node tgt) {
    source = src;
    target = tgt;
  }

  public Node getSource() {
    return source;
  }

  public Node getTarget() {
    return target;
  }
}
