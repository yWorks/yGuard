package com.yworks.graph;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Network;

import java.util.Set;

/**
 * The type Edge.
 */
public class Edge {
    private Network<Node, Edge> network;

    /**
     * Instantiates a new Edge.
     *
     * @param network the network
     */
    public Edge(Network<Node, Edge> network) {
        this.network = network;
    }

    /**
     * Returns the target node of the edge.
     *
     * @return {Node}
     */
    public Node target() {
        return this.network.incidentNodes(this).target();
    }

    /**
     * Returns the source node of the edge
     *
     * @return {Node}
     */
    public Node source() {
        return this.network.incidentNodes(this).source();
    }

    /**
     * Returns the next edge (in insertion order) connecting source and target node, if any.
     *
     * @return {Edge|null}
     */
    public Edge nextInEdge() {
        Set<Edge> inEdges = this.network.inEdges(this.target());
        boolean found = false;
        for (final Edge edge: inEdges) {
            if (found) {
                return edge;
            } else if (edge.equals(this)) {
                found = true;
            }
        }
        return null;
    }

    /**
     * Returns the next edge (in insertion order) that is outgoing of target node, if any.
     *
     * @return {Edge|null}
     */
    public Edge nextOutEdge() {
        Set<Edge> outEdges = this.network.outEdges(this.network.incidentNodes(this).source());
        boolean found = false;
        for (final Edge edge: outEdges) {
            if (found) {
                return edge;
            } else if (edge.equals(this)) {
                found = true;
            }
        }
        return null;
    }

    /**
     * Returns the edge going in opposite direction of this edge in insertion order, respectively to the given node, if any.
     *
     * @param node the node
     * @return {Node}
     */
    public Node opposite(Node node) {
        EndpointPair<Node> endpointPair = this.network.incidentNodes(this);
        if (endpointPair.source().equals(node)) {
            return endpointPair.target();
        } else {
            return endpointPair.source();
        }
    }
}
