package com.yworks.yshrink.model;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class NodeType {

  public static final int METHOD      = 0x0001;
  public static final int FIELD       = 0x0002;
  public static final int CLASS       = 0x0004;
  public static final int INTERFACE   = 0x0010;
  public static final int NEW         = 0x0020;
  public static final int ENTRYPOINT  = 0x0040;
  public static final int STATIC      = 0x0100;
  public static final int OBSOLETE    = 0x2000;
  public static final int STUB        = 0x4000;


  private NodeType() {
  }

//  public static boolean isReachable( final int nodeType ) {
//    return ( nodeType & REACHABLE ) != 0;
//  }

  public static boolean isObsolete( final int nodeType ) {
    return ( nodeType & OBSOLETE ) == OBSOLETE;
  }

  public static boolean isStatic( final int nodeType ) {
    return ( nodeType & STATIC ) == STATIC;
  }

  public static boolean isStubNeeded( final int nodeType ) {
    return ( nodeType & STUB ) == STUB;
  }

  public static boolean isMethodNode( final int nodeType ) {
    return ( nodeType & METHOD ) == METHOD;
  }

  public static boolean isInterfaceNode( final int nodeType ) {
    return ( nodeType & INTERFACE ) == INTERFACE;
  }

  public static boolean isNewNode( int nodeType ) {
    return ( nodeType & NEW ) == NEW;
  }
}
