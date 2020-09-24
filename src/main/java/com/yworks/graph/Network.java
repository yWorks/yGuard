package com.yworks.graph;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Network<N, E> {
  Map<N, Set<N>> out = new HashMap<>();
  Map<N, Set<N>> in = new HashMap<>();
  Map<Map.Entry<N, N>, E> edges = new HashMap<>();

  public void addEdge(N source, N target, E edge) {
    addNode(source, target);
    out.get(source).add(target);
    in.get(target).add(source);
    edges.put(new AbstractMap.SimpleImmutableEntry<>(source, target), edge);
  }

  public Set<E> inEdges( N node) {
    Set<E> inEdges = new HashSet<>();
    for (N in: in.get(node)) {
      inEdges.add(edges.get(new AbstractMap.SimpleImmutableEntry<N, N>(in, node)));
    }
    return inEdges;
  }

  public Set<E> outEdges( N node) {
    Set<E> outEdges = new HashSet<>();
    for (N out: out.get(node)) {
      outEdges.add(edges.get(new AbstractMap.SimpleImmutableEntry<>(node, out)));
    }
    return outEdges;
  }

  public Set<E> edgesConnecting( N source, N target) {
    Set<E> connectingEdges = new HashSet<>();
    for (N out: out.get(source)) {
      if (out.equals(target)) {
        connectingEdges.add(edges.get(new AbstractMap.SimpleImmutableEntry<N, N>(source, target)));
      }
    }
    return connectingEdges;
  }

  private void addNode(N... nodes) {
    for (N node: nodes) {
      if (!out.containsKey(node)) out.put(node, new HashSet<N>());
      if (!in.containsKey(node)) in.put(node, new HashSet<N>());
    }
  }

  public Set<N> nodes() {
    Set<N> nodes = new HashSet<>(out.keySet());
    nodes.addAll(in.keySet());
    return nodes;
  }

  public Map.Entry<N, N> incidentNodes( E edge) {
    for (Map.Entry<Map.Entry<N, N>, E> entry : edges.entrySet()) {
      if (entry.getValue().equals(edge)) return entry.getKey();
    }
    return null;
  }

}
