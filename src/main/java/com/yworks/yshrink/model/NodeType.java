package com.yworks.yshrink.model;

/**
 * The type Node type.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class NodeType {

  /**
   * The constant METHOD.
   */
  public static final int METHOD      = 0x0001;
  /**
   * The constant FIELD.
   */
  public static final int FIELD       = 0x0002;
  /**
   * The constant CLASS.
   */
  public static final int CLASS       = 0x0004;
  /**
   * The constant INTERFACE.
   */
  public static final int INTERFACE   = 0x0010;
  /**
   * The constant NEW.
   */
  public static final int NEW         = 0x0020;
  /**
   * The constant ENTRYPOINT.
   */
  public static final int ENTRYPOINT  = 0x0040;
  /**
   * The constant STATIC.
   */
  public static final int STATIC      = 0x0100;
  /**
   * The constant OBSOLETE.
   */
  public static final int OBSOLETE    = 0x2000;
  /**
   * The constant STUB.
   */
  public static final int STUB        = 0x4000;


  private NodeType() {
  }

//  public static boolean isReachable( final int nodeType ) {
//    return ( nodeType & REACHABLE ) != 0;
//  }

  /**
   * Is obsolete boolean.
   *
   * @param nodeType the node type
   * @return the boolean
   */
  public static boolean isObsolete( final int nodeType ) {
    return ( nodeType & OBSOLETE ) == OBSOLETE;
  }

  /**
   * Is static boolean.
   *
   * @param nodeType the node type
   * @return the boolean
   */
  public static boolean isStatic( final int nodeType ) {
    return ( nodeType & STATIC ) == STATIC;
  }

  /**
   * Is stub needed boolean.
   *
   * @param nodeType the node type
   * @return the boolean
   */
  public static boolean isStubNeeded( final int nodeType ) {
    return ( nodeType & STUB ) == STUB;
  }

  /**
   * Is method node boolean.
   *
   * @param nodeType the node type
   * @return the boolean
   */
  public static boolean isMethodNode( final int nodeType ) {
    return ( nodeType & METHOD ) == METHOD;
  }

  /**
   * Is interface node boolean.
   *
   * @param nodeType the node type
   * @return the boolean
   */
  public static boolean isInterfaceNode( final int nodeType ) {
    return ( nodeType & INTERFACE ) == INTERFACE;
  }

  /**
   * Is new node boolean.
   *
   * @param nodeType the node type
   * @return the boolean
   */
  public static boolean isNewNode( int nodeType ) {
    return ( nodeType & NEW ) == NEW;
  }
}
