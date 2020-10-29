package com.yworks.util.graph;

import java.util.Iterator;

/**
 * Describes a network used in DFS analysis by the Shrinker.
 * This is a directed graph which is capable of addition.
 * A default implementation exists, but more elaborate backends can be used as well.
 */
public interface Network {
  /**
   * Creates a new node object
   * @return node
   */
  Object createNode();

  /**
   * Creates a new edge object
   * @param source - the source node
   * @param target - the target node
   * @return edge
   */
  Object createEdge(Object source, Object target);

  /**
   * Get the source node of an edge
   * @param edge - the edge to get the source of
   * @return node
   */
  Object getSource(Object edge);

  /**
   * Get the target node of an edge
   * @param edge - the edge to get the target of
   * @return node
   */
  Object getTarget(Object edge);

  /**
   * Retrieve an iterator to the collection of nodes this network has
   * @return nodes[]
   */
  Iterator nodes();

  /**
   * Retrieve an iterator to the collection of egdes this network has
   * @return edges[]
   */
  Iterator edges();

  /**
   * How many nodes this network has
   * @return number of nodes
   */
  Integer nodesSize();

  /**
   * Returns all ingoing edges of this node.
   * @return edge[]
   */
  Iterator inEdges(Object node);

  /**
   * Returns all outgoing edges of this node.
   * @return edge[]
   */
  Iterator outEdges(Object node);

  /**
   * Returns the first ingoing edge for this node, if any.
   * @return {edge|null}
   */
  Object firstInEdge(Object node);

  /**
   * Returns the first outgoing edge for this node, if any.
   * @return {edge|null}
   */
  Object firstOutEdge(Object node);

  /**
   * Returns the next edge (in insertion order) connecting source and target node, if any.
   * @param edge - the edge for which to retrieve incoming edges
   * @return {edge|null}
   */
  Object nextInEdge(Object edge);

  /**
   * Returns the next edge (in insertion order) that is outgoing of target node, if any.
   * @param edge - the edge for which to retrieve outgoing edges
   * @return {edge|null}
   */
  Object nextOutEdge(Object edge);

  Iterator edgesConnecting(Object source, Object target);

  /**
   * Returns the edge going in opposite direction of this edge in insertion order, respectively to the given node, if any.
   * @param node - the node for which to get the opposite node
   * @return {Node}
   */
  Object opposite(Object edge, Object node);
}
