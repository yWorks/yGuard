package com.yworks.util.graph;

/**
 * Package-private class used by DefaultNetwork to represent an Edge.
 */
class Edge {
  private final Node source, target;
  private Edge nextInEdge, nextOutEdge = null;

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

  public Edge getNextInEdge() {
    return nextInEdge;
  }

  public Edge getNextOutEdge() {
    return nextOutEdge;
  }

  public void setNextInEdge( final Edge nextInEdge ) {
    this.nextInEdge = nextInEdge;
  }

  public void setNextOutEdge( final Edge nextOutEdge ) {
    this.nextOutEdge = nextOutEdge;
  }
}
