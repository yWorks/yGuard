package com.yworks.yshrink.core;

import com.yworks.graph.Edge;
import com.yworks.graph.Node;
import com.google.common.graph.Network;

import java.util.HashMap;
import java.util.Map;

/**
 * Framework class for depth first search (DFS) based algorithms. To write graph algorithms that are based on a depth
 * first search one can extend this class and overwrite appropriate callback methods provided by this class.
 *
 * @author Roland Wiese (RW)
 */

public class Dfs {

  private Map<Object, Object> edgeVisit;

  private int dfsNum;
  private int compNum;

  private boolean directedMode;

  /**
   * NodeMap that indicates the state of the nodes as they are visited by this algorithm. Possible states of a node are
   * {@link #WHITE WHITE}, {@link #GRAY GRAY} and {@link #BLACK BLACK}.
   */
  protected Map<Object, Object> stateMap;

  /**
   * Node state specifier. Indicates that a node was not yet visited.
   */
  protected static Object WHITE = null;

  /**
   * Node state specifier. Indicates that a node was already visited but has not been completed yet, i.e. it is still
   * part of an active path of the dfs tree.
   */
  protected static Object GRAY = new Object();

  /**
   * Node state specifier. Indicates that the node has been completed, i.e. it has been visited before and is not part
   * of an active path in the dfs tree anymore.
   */
  protected static Object BLACK = new Object();

  /**
   * Instantiates a new Dfs object.
   */
  public Dfs() {
    directedMode = false;
  }

  /**
   * Whether or not to interpret the edges of the graph as directed.
   * By default directed mode is disabled.
   */
  public void setDirectedMode( final boolean directed ) {
    directedMode = directed;
  }

  /**
   * Starts a depth first search on the given graph. The given node will be visited first. If <code>start</code> is
   * null, this method returns silently.
   */
  public void start( final Network<Node, Edge> network, final Node start ) {
    if ( null == start ) return;

    stateMap = new HashMap<>();
    if ( !directedMode ) {
      edgeVisit = new HashMap<>();
    }

    dfsNum = 0;
    compNum = 0;

    final int stackSize = Math.min( 60, network.nodes().size() + 3 );
    Stack stack = new Stack( stackSize );

    try {
      workStack( stack, start );
//      for ( NodeCursor nodeCursor = starts.nodes(); nodeCursor.ok(); nodeCursor.next() ) {
//        Node node = nodeCursor.node();
//        if ( stateMap.get( node ) != BLACK ) {
//
//        }
//      }
    } finally {
      stateMap.clear();
      if ( !directedMode ) {
        edgeVisit.clear();
      }
    }
  }

  private Edge nextEdge( final Node currentNode, final Edge currentEdge, final byte[] currentMode ) {

    switch ( currentMode[ 0 ] ) {

      case 0:
        if ( directedMode ) {
          currentMode[ 0 ] = 1;

          // return null to force finish
          return currentNode.firstOutEdge();
        } else {
          Edge edge = currentNode.firstOutEdge();
          if ( edge == null ) {
            edge = currentNode.firstInEdge();
            currentMode[ 0 ] = 3;
          } else {
            currentMode[ 0 ] = 2;
          }
          return edge;
        }
      case 1:
        // return null to force finish
        return currentEdge.nextOutEdge();
      case 2: {
        Edge edge = currentEdge.nextOutEdge();
        if ( edge == null ) {
          edge = currentNode.firstInEdge();
          currentMode[ 0 ] = 3;
        }
        return edge;
      }
      case 3:
        return currentEdge.nextInEdge();
      default:
        throw new InternalError();
    }
  }

  private Edge doNextEdge( final Node currentNode, final Edge currentEdge, final byte[] currentMode ) {

    Edge edge = nextEdge( currentNode, currentEdge, currentMode );

    while ( edge != null && !doTraverse( edge ) ) {
      edge = nextEdge( currentNode, edge, currentMode );
    }

    return edge;
  }

  private byte[] nextState = new byte[1];

  private void workStack( final Stack stack, final Node start ) {
    nextState[ 0 ] = 0;
    Node currentNode = start;
    stateMap.put( currentNode, GRAY);
    preVisit( currentNode, ++dfsNum );

    {
      final Edge nextEdge = doNextEdge( currentNode, null, nextState );
      stack.pushState( currentNode, nextEdge, nextState[ 0 ], dfsNum );
    }

    while ( !stack.isEmpty() ) {

      Edge edge = stack.peekCurrentEdge();
      nextState[ 0 ] = stack.peekIteratorState();

      while ( edge != null ) {

        if ( directedMode || !(boolean)edgeVisit.get( edge ) ) {
          final Node other;
          if ( !directedMode ) {
            edgeVisit.put( edge, true );
            other = edge.opposite( currentNode );
          } else {
            other = edge.target();
          }
          if ( stateMap.get( other ) == null ) {

            // !
            preTraverse( edge, other, true );

            stateMap.put( other, GRAY );
            currentNode = other;
            preVisit( currentNode, ++dfsNum );
            {
              nextState[ 0 ] = 0;
              edge = doNextEdge( currentNode, null, nextState );

              stack.pushState( currentNode, edge, nextState[ 0 ], dfsNum );
            }
          } else {

            // !
            preTraverse( edge, other, false );
            {
              edge = doNextEdge( currentNode, edge, nextState );

              stack.updateTop( edge, nextState[ 0 ] );
            }
          }
        } else {

          // !
          edge = doNextEdge( currentNode, edge, nextState );

          stack.updateTop( edge, nextState[ 0 ] );
        }
      }
      postVisit( currentNode, stack.peekLocalDfsNum(), ++compNum );
      stateMap.put( currentNode, BLACK );
      stack.pop();
      if ( !stack.isEmpty() ) {
        final Edge currentEdge = stack.peekCurrentEdge();
        postTraverse( currentEdge, currentNode );
        currentNode = stack.peekNode();
        nextState[ 0 ] = stack.peekIteratorState();
        {
          final Edge nextEdge = doNextEdge( currentNode, currentEdge, nextState );

          stack.updateTop( nextEdge, nextState[ 0 ] );
        }
      }
    }
  }

  /**
   * Callback method that will be invoked whenever a formerly unvisited node gets visited the first time. The given int
   * is the dfsnumber of that node.
   * By default this method does nothing
   */
  protected void preVisit( final Node node, final int dfsNumber ) {
  }

  /**
   * Callback method that will be invoked whenever a node visit has been completed. The dfs number and the completion
   * number of the given node will be passed in. By default this method does nothing
   */
  protected void postVisit( final Node node, final int dfsNumber, final int compNumber ) {
  }

  /**
   * Callback method that will be invoked if the given edge will be looked at in the search the first (and only) time.
   * The given node is the node that will be visited next iff <CODE>treeEdge == true</CODE>. By default this method does
   * nothing
   */
  protected boolean preTraverse( final Edge edge, final Node node, final boolean treeEdge ) {
    return true;
  }

  /**
   * Callback method that will be invoked after the search returns from the given node. The node has been reached via
   * the given edge. By default this method does nothing.
   */
  protected void postTraverse( final Edge edge, final Node node ) {
  }

  protected boolean doTraverse( final Edge e ) {
    return true;
  }

  static class Stack {
    int stackIndex = -1;
    byte[] iteratorStates;
    Edge[] currentEdges;
    int[] localDfsNums;
    Node[] nodes;

    Stack( final int initialSize ) {
      localDfsNums = new int[initialSize];
      currentEdges = new Edge[initialSize];
      iteratorStates = new byte[initialSize];
      nodes = new Node[initialSize];
    }

    boolean isEmpty() {
      return stackIndex < 0;
    }

    void pop() {
      stackIndex--;
    }

    Node peekNode() {
      return nodes[ stackIndex ];
    }

    Edge peekCurrentEdge() {
      return currentEdges[ stackIndex ];
    }

    byte peekIteratorState() {
      return iteratorStates[ stackIndex ];
    }

    int peekLocalDfsNum() {
      return localDfsNums[ stackIndex ];
    }

    int pushState( final Node node, final Edge currentEdge, final byte iterastorState, final int localDfsNum ) {
      stackIndex++;
      if ( stackIndex == nodes.length ) {
        final int newSize = ( stackIndex + 1 ) * 2;
        final Node[] newStack = new Node[newSize];
        System.arraycopy( nodes, 0, newStack, 0, nodes.length );
        this.nodes = newStack;
        final Edge[] newEStack = new Edge[newSize];
        System.arraycopy( currentEdges, 0, newEStack, 0, currentEdges.length );
        this.currentEdges = newEStack;
        final int[] newDStack = new int[newSize];
        System.arraycopy( localDfsNums, 0, newDStack, 0, localDfsNums.length );
        this.localDfsNums = newDStack;
        final byte[] newStateStack = new byte[newSize];
        System.arraycopy( iteratorStates, 0, newStateStack, 0, iteratorStates.length );
        this.iteratorStates = newStateStack;
      }
      this.nodes[ stackIndex ] = node;
      this.currentEdges[ stackIndex ] = currentEdge;
      this.iteratorStates[ stackIndex ] = iterastorState;
      return this.localDfsNums[ stackIndex ] = localDfsNum;
    }

    void updateTop( final Edge currentEdge, final byte iteratorState ) {
      this.currentEdges[ stackIndex ] = currentEdge;
      this.iteratorStates[ stackIndex ] = iteratorState;
    }
  }
}

