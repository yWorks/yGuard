package com.yworks.yshrink.model;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public enum EdgeType {

  /**
   * EXTENDS edges from each class to its superclass, if the superclass is contained in the model.
   */
  EXTENDS,
  /**
   * IMPLEMENTS edges from each class to all of its interfaces, if the interface is contained in the model.
   */
  IMPLEMENTS,
  /**
   * INVOKE edges to all implementations of the target method in any sublcasses of the target class.
   */
  INVOKES,
  /**
   * RESOLVE edges are created from each method to its return type, exception types and argument types.
   */
  RESOLVE,
  /**
   * REFERENCES edge is created from the referencing method to the field in the class the declares the field.
   * If the field is not declared in the runtime class, the superclasses and interfaces of the runtime class are searched for the declaration. While searching for the declaration, RESOLVE edges are created to the visited concrete classes.
   */
  REFERENCES,
  /**
   * ENCLOSE edge from each inner class to its enclosing class or method.
   */
  ENCLOSE,
  /**
   * MEMBER_OF edges from each method, field and from the special NEW-node to the class it belongs to.
   */
  MEMBER_OF,
  /**
   * If a class inherits from any external class that cannot be resolved, ASSUME edges are created from the NEW-node to every none-private method.
   */
  ASSUME,
  /**
   * For constuctor calls, an ordinary INVOKE edge is created to the specific <init> method.
   * Additionally, a special CREATES Edge is created to the special NEW node in [Method: com.yworks.yshrink.core.Analyzer.createTypeInstructionEdges()], indicating that the target class is instantiated.
   */
  CREATES,
  /**
   * If a call is a "chain" call to the super-constructor, a special CHAIN edge is created.
   */
  CHAIN,
  /**
   * Otherwise a SUPER edge is created to the implementation of the target method in the first super class of the target class that implements the target method.
   */
  SUPER,
  /**
   * A single ENTRYPOINT node is created for the inOutPair that is currently processed
   */
  ENTRYPOINT,
  /**
   * A INVOKEDYNAMIC edge is created for `invokedynamic` instruction calls, referencing the class and method that implement the functionality if this is covered by the model.
   */
  INVOKEDYNAMIC
}
