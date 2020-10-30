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
    return node;
  }

  @Override
  public Object createEdge( final Object source, final Object target ) {
    Node src = (Node) source;
    Node tgt = (Node) target;
    Edge edge = new Edge(src, tgt);
    if (src.getOutEdges().size() > 0) src.getOutEdges().get(src.getOutEdges().size() - 1).setNextOutEdge(edge);
    src.addOutEdge(edge);
    if (tgt.getInEdges().size() > 0) tgt.getInEdges().get(tgt.getInEdges().size() - 1).setNextInEdge(edge);
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
    Node n = (Node) node;
    return n.getInEdges().size() > 0 ? n.getInEdges().get(0) : null;
  }

  @Override
  public Object firstOutEdge( final Object node ) {
    Node n = (Node) node;
    return n.getOutEdges().size() > 0 ? n.getOutEdges().get(0) : null;
  }

  @Override
  public Object nextInEdge( final Object edge ) {
    return ((Edge) edge).getNextInEdge();
  }

  @Override
  public Object nextOutEdge( final Object edge ) {
    return ((Edge) edge).getNextOutEdge();
  }

  @Override
  public Iterator edgesConnecting(Object source, Object target) {
    Node src = (Node) source;
    List<Edge> edgesConnecting = new ArrayList<>(src.getInEdges().size());
    for (Edge e: src.getOutEdges()) {
      if (e.getTarget().equals(target)) edgesConnecting.add(e);
    }
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
