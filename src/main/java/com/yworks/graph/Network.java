package com.yworks.graph;

import java.util.Iterator;

public interface Network {
  // Create nodes and edges
  Object createNode();
  Object createEdge(Object source, Object target);

  // Get source and target of edges
  Object getSource(Object edge);
  Object getTarget(Object edge);

  Iterator nodes();
  Iterator edges();
  Integer nodesSize();
  Integer edgesSize();

  Iterator inEdges(Object node);
  Iterator outEdges(Object node);

  Object firstInEdge(Object node);
  Object firstOutEdge(Object node);

  Object nextInEdge(Object edge);
  Object nextOutEdge(Object edge);

  Iterator edgesConnecting(Object source, Object target);
  Object opposite(Object edge, Object node);
}
