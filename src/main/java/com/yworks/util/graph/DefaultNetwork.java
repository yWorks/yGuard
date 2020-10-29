package com.yworks.util.graph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultNetwork implements Network {
  private final List<Node> nodes = new ArrayList<>();
  private final List<Edge> edges = new ArrayList<>();

  @Override
  public Object createNode() {
    Node node = new Node();
    nodes.add(node);
    return (Object) node;
  }

  @Override
  public Object createEdge( final Object source, final Object target ) {
    Node src = (Node) source;
    Node tgt = (Node) target;
    Edge edge = new Edge(src, tgt);
    src.addOutEdge(edge);
    tgt.addInEdge(edge);
    edges.add(edge);
    return edge;
  }

  @Override
  public Object getSource( final Object edge ) {
    return ((Edge)edge).getSource();
  }

  @Override
  public Object getTarget( final Object edge ) {
    return ((Edge)edge).getTarget();
  }

  @Override
  public Iterator nodes() {
    return nodes.iterator();
  }

  @Override
  public Integer nodesSize() {
    return nodes.size();
  }

  @Override
  public Iterator edges() {
    return edges.iterator();
  }

  @Override
  public Iterator inEdges( final Object node ) {
    return ((Node)node).getInEdges().iterator();
  }

  @Override
  public Iterator outEdges( final Object node ) {
    return ((Node)node).getOutEdges().iterator();
  }

  @Override
  public Object firstInEdge( final Object node ) {
    Iterator inEdgesIterator = inEdges(node);
    if (inEdgesIterator.hasNext()) {
      return inEdgesIterator.next();
    }
    return null;
  }

  @Override
  public Object firstOutEdge( final Object node ) {
    Iterator outEdgesIterator = outEdges(node);
    if (outEdgesIterator.hasNext()) {
      return outEdgesIterator.next();
    }
    return null;
  }

  @Override
  public Object nextInEdge( final Object edge ) {
    Iterator inEdgesIterator = inEdges(getTarget(edge));
    boolean found = false;
    Object currentEdge = null;
    while (inEdgesIterator.hasNext()) {
      currentEdge = inEdgesIterator.next();
      if (found) {
        return currentEdge;
      } else if (edge.equals(currentEdge)) {
        found = true;
      }
    }
    return null;
  }

  @Override
  public Object nextOutEdge( final Object edge ) {
    Iterator outEdgesIterator = outEdges(getSource(edge));
    boolean found = false;
    Object currentEdge = null;
    while (outEdgesIterator.hasNext()) {
      currentEdge = outEdgesIterator.next();
      if (found) {
        return currentEdge;
      } else if (edge.equals(currentEdge)) {
        found = true;
      }
    }
    return null;
  }

  @Override
  public Iterator edgesConnecting(Object source, Object target) {
    Node src = (Node) source;
    Node tgt = (Node) target;
    List<Edge> edgesConnecting = new ArrayList<>(src.getOutEdges());
    edgesConnecting.retainAll(tgt.getInEdges());
    return edgesConnecting.iterator();
  }

  @Override
  public Object opposite( final Object edge, final Object node ) {
    if (getSource(edge).equals(node)) {
      return getTarget(edge);
    } else {
      return getSource(edge);
    }
  }
}
