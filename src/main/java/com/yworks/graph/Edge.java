package com.yworks.graph;

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
