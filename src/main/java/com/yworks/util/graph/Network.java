package com.yworks.util.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Network<N, E> {
  Map<N, Set<N>> out = new HashMap<>();
  Map<N, Set<N>> in = new HashMap<>();
  Map<EndpointPair<N>, Set<E>> edges = new HashMap<>();

  public void addEdge(N source, N target, E edge) {
    addNode(source, target);
    out.get(source).add(target);
    in.get(target).add(source);
    EndpointPair endpointPair = newPair(source, target);
    addEdge(endpointPair);
    edges.get(endpointPair).add(edge);
  }

  public Set<E> inEdges( N node) {
    Set<E> inEdges = new HashSet<>();
    for (N in: in.get(node)) {
      inEdges.addAll(getEdges(in, node));
    }
    return inEdges;
  }

  public Set<E> outEdges( N node) {
    Set<E> outEdges = new HashSet<>();
    for (N out: out.get(node)) {
      outEdges.addAll(getEdges(node, out));
    }
    return outEdges;
  }

  public Set<E> edgesConnecting( N source, N target) {
    Set<E> connectingEdges = new HashSet<>();
    for (N out: out.get(source)) {
      if (out.equals(target)) {
        connectingEdges.addAll(getEdges(source, target));
      }
    }
    return connectingEdges;
  }

  public void addNode(N... nodes) {
    for (N node: nodes) {
      if (!out.containsKey(node)) out.put(node, new HashSet<N>());
      if (!in.containsKey(node)) in.put(node, new HashSet<N>());
    }
  }

  public void addEdge(EndpointPair<N> endpointPair) {
    if (!edges.containsKey(endpointPair)) {
      edges.put(endpointPair, new HashSet<E>());
    };
  }

  public Set<N> nodes() {
    Set<N> nodes = new HashSet<>(out.keySet());
    nodes.addAll(in.keySet());
    return nodes;
  }

  public EndpointPair<N> incidentNodes( E edge) {
    for (Map.Entry<EndpointPair<N>, Set<E>> entry : edges.entrySet()) {
      if (entry.getValue().contains(edge)) {
        return entry.getKey();
      }
    }
    return null;
  }

  private Set<E> getEdges( final N source, final N target ) {
    return edges.get(newPair(source, target));
  }

  private static <N> EndpointPair<N> newPair( N source, N target ) {
    return new EndpointPair<>(source, target);
  }
}
