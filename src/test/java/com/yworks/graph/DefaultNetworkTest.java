package com.yworks.graph;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultNetworkTest extends TestCase {

  public void testCreateNode() {
    Network network = new DefaultNetwork();
    Object node = network.createNode();
    assertEquals(network.nodes().next(), node);
  }

  public void testCreateEdge() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.edges().next(), edge);
  }

  public void testGetSource() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.getSource(edge), src);
  }

  public void testGetTarget() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.getTarget(edge), tgt);
  }

  public void testNodes() {
    Network network = new DefaultNetwork();
    List<Object> nodeList = new ArrayList<>();
    for (int i = 0; i < 5; i++) nodeList.add(network.createNode());
    List<Object> convertedList = new ArrayList<>(nodeList.size());
    Iterator nodeIterator = network.nodes();
    while (nodeIterator.hasNext()) convertedList.add(nodeIterator.next());
    assertEquals(nodeList, convertedList);
  }

  public void testEdges() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    List<Object> edgeList = new ArrayList<>();
    for (int i = 0; i < 5; i++) edgeList.add(network.createEdge(src, tgt));
    List<Object> convertedEdges = new ArrayList<>(edgeList.size());
    Iterator edgeIterator = network.edges();
    while (edgeIterator.hasNext()) convertedEdges.add(edgeIterator.next());
    assertEquals(edgeList, convertedEdges);
  }

  public void testInEdges() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.inEdges(tgt).next(), edge);
  }

  public void testOutEdges() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.outEdges(src).next(), edge);
  }

  public void testFirstInEdge() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge1 = network.createEdge(src, tgt);
    Object edge2 = network.createEdge(src, tgt);
    assertEquals(network.firstInEdge(tgt), edge1);
  }

  public void testFirstOutEdge() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    Object edge2 = network.createEdge(src, tgt);
    assertEquals(network.firstOutEdge(src), edge);
  }

  public void testNextInEdge() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    Object edge2 = network.createEdge(src, tgt);
    assertEquals(network.nextInEdge(edge), edge2);
  }

  public void testNextOutEdge() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    Object edge2 = network.createEdge(src, tgt);
    assertEquals(network.nextOutEdge(edge), edge2);
  }

  public void testEdgesConnecting() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.edgesConnecting(src, tgt).next(), edge);
  }

  public void testOpposite() {
    Network network = new DefaultNetwork();
    Object src = network.createNode();
    Object tgt = network.createNode();
    Object edge = network.createEdge(src, tgt);
    assertEquals(network.opposite(edge, src), tgt);
    assertEquals(network.opposite(edge, tgt), src);
  }
}