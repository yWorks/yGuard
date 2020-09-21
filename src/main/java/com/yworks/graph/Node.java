package com.yworks.graph;

import com.google.common.graph.Network;

import java.util.Iterator;
import java.util.Set;

public class Node {
    private Network<Node, Edge> network;

    public Node(Network<Node, Edge> network) {
        this.network = network;
    }

    /**
     * Returns all outgoing edges of this node.
     */
    public Set<Edge> outEdges() {
        return this.network.outEdges(this);
    }

    /**
     * Returns all ingoing edges of this node.
     */
    public Set<Edge> inEdges() {
        return this.network.inEdges(this);
    }

    /**
     * Returns the first outgoing edge for this node, if any.
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
     * @return {Edge|null}
     */
    public Edge firstInEdge() {
        Iterator<Edge> inEdgesIterator = this.network.inEdges(this).iterator();
        if (inEdgesIterator.hasNext()) {
            return inEdgesIterator.next();
        }
        return null;
    }

    /**
     * Returns the edge directly connecting to this node, if any.
     * @param node - the node which you would like to know the edge for.
     * @return {Edge|null}
     */
    public Edge getEdgeTo(Node node) {
        Set<Edge> connectingEdges = this.network.edgesConnecting(this, node);
        if (connectingEdges.iterator().hasNext()) {
            return connectingEdges.iterator().next();
        }
        return null;
    }
}
