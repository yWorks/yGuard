package com.yworks.util.graph;


import java.util.Iterator;
import java.util.Set;

/**
 * The type Node.
 */
public class Node {
    private Network<Node, Edge> network;

  /**
   * Instantiates a new Node.
   *
   * @param network the network
   */
  public Node(Network<Node, Edge> network) {
        this.network = network;
    }

  /**
   * Returns all outgoing edges of this node.
   *
   * @return the set
   */
  public Set<Edge> outEdges() {
        return this.network.outEdges(this);
    }

  /**
   * Returns all ingoing edges of this node.
   *
   * @return the set
   */
  public Set<Edge> inEdges() {
        return this.network.inEdges(this);
    }

  /**
   * Returns the first outgoing edge for this node, if any.
   *
   * @return {com.google.common.graph.Network.Edge|null}
   */
  public Edge firstOutEdge() {
        Iterator<Edge> outEdgesIterator = this.network.outEdges(this).iterator();
        if (outEdgesIterator.hasNext()) {
            return outEdgesIterator.next();
        }
        return null;
    }

  /**
   * Returns the first ingoing edge for this node, if any.
   *
   * @return {Edge|null}
   */
  public Edge firstInEdge() {
        Iterator<Edge> inEdgesIterator = this.network.inEdges(this).iterator();
        if (inEdgesIterator.hasNext()) {
            return inEdgesIterator.next();
        }
        return null;
    }

}
