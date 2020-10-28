package com.yworks.util.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Network<N, E> {
  Map<N, Set<N>> out = new HashMap<>();
  Map<N, Set<N>> in = new HashMap<>();
  Map<N, Set<E>> inEdges = new HashMap<>();
  Map<N, Set<E>> outEdges = new HashMap<>();

  // NOTE: This occupies double the amount of memory but is worth the sacrifice because it
  // reduces runtime from potentially O(n^2) to O(1)
  Map<EndpointPair<N>, Set<E>> edges = new HashMap<>();
  Map<E, EndpointPair<N>> reverseEdges = new HashMap<>();

  public void addEdge(N source, N target, E edge) {
    addNode(source, target);
    out.get(source).add(target);
    in.get(target).add(source);
    EndpointPair endpointPair = newPair(source, target);
    addEdge(endpointPair);
    edges.get(endpointPair).add(edge);
    inEdges.get(target).add(edge);
    outEdges.get(source).add(edge);
    reverseEdges.put(edge, endpointPair);
  }

  public Set<E> inEdges( N node) {
    if (inEdges.containsKey(node)) return inEdges.get(node);
    return new HashSet<>();
  }

  public Set<E> outEdges( N node) {
    if (outEdges.containsKey(node)) return outEdges.get(node);
    return new HashSet<>();
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
    }
    if (!inEdges.containsKey(endpointPair.target())) {
      inEdges.put(endpointPair.target(), new HashSet<E>());
    }
    if(!outEdges.containsKey(endpointPair.source())) {
      outEdges.put(endpointPair.source(), new HashSet<E>());
    }
  }

  public Set<N> nodes() {
    Set<N> nodes = new HashSet<>(out.keySet());
    nodes.addAll(in.keySet());
    return nodes;
  }

  public EndpointPair<N> incidentNodes( E edge) {
    return reverseEdges.get(edge);
  }

  private Set<E> getEdges( final N source, final N target ) {
    return edges.get(newPair(source, target));
  }

  private static <N> EndpointPair<N> newPair( N source, N target ) {
    return new EndpointPair<>(source, target);
  }
}
