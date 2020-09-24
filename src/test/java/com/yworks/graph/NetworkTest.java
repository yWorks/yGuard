package com.yworks.graph;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class NetworkTest {
  @Test
  public void testOutEdges() {
    Network<Integer, Integer> network = new Network<>();
    Integer n1 = 1;
    Integer n2 = 2;
    Integer e1 = 3;
    network.addEdge(n1, n2, e1);
    Set<Integer> outEdges = network.outEdges(n1);
    assertEquals(outEdges.size(), 1);
    assertEquals(outEdges.iterator().next(), e1);
  }

  @Test
  public void testInEdges() {
    Network<Integer, Integer> network = new Network<>();
    Integer n1 = 1;
    Integer n2 = 2;
    Integer e1 = 3;
    network.addEdge(n1, n2, e1);
    Set<Integer> inEdges = network.inEdges(n2);
    assertEquals(inEdges.size(), 1);
    assertEquals(inEdges.iterator().next(), e1);
  }

  @Test
  public void testConnectingEdges() {
    Network<Integer, Integer> network = new Network<>();
    Integer n1 = 1;
    Integer n2 = 2;
    Integer e1 = 3;
    network.addEdge(n1, n2, e1);
    Set<Integer> connectingEdges = network.edgesConnecting(n1, n2);
    assertEquals(connectingEdges.size(), 1);
    assertEquals(connectingEdges.iterator().next(), e1);
    assertEquals(network.edgesConnecting(n2, n1).size(), 0);
  }

  @Test
  public void testIncidentNodes() {
    Network<Integer, Integer> network = new Network<>();
    Integer n1 = 1;
    Integer n2 = 2;
    Integer e1 = 3;
    network.addEdge(n1, n2, e1);
    Map.Entry<Integer, Integer> pair = network.incidentNodes(e1);
    assertEquals(pair.getKey(), n1);
    assertEquals(pair.getValue(), n2);
  }
}
