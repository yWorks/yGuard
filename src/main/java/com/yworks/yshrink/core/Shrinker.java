package com.yworks.yshrink.core;

import com.yworks.yshrink.model.AbstractDescriptor;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.EdgeType;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.model.NodeType;
import com.yworks.util.graph.Network;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * The type Shrinker.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Shrinker {

  //private Model model;

  /**
   * Shrink.
   *
   * @param model the model
   */
  public void shrink( final Model model ) {
    //this.model = model;

    final ShrinkDfs shrinkDfs = new ShrinkDfs( model );
    shrinkDfs.setDirectedMode( true );

    // initially mark all nodes OBSOLETE
    Iterator nodeIterator = model.getNetwork().nodes();
    while (nodeIterator.hasNext()) {
      Object node = nodeIterator.next();
      model.markObsolete( node );
    }

    shrinkDfs.init( model.getEntryPointNode() );

    int numInstantiated = -1;
    while ( shrinkDfs.numInstantiated > numInstantiated ) {
      numInstantiated = shrinkDfs.numInstantiated;
      shrinkDfs.nextRound();
    }

    shrinkDfs.markReachableNodes();
  }

  private class ShrinkDfs extends Dfs {

    private Model model;
    private Network network;
    private Object entryPointNode;
    private Map<Object, Object> instanceMap;
    private int numInstantiated = 0;
    private int round = 0;
    private int numSkipped = 0;

    private static final int EXPLORE_MODE = 0;
    private static final int RESULT_MODE = 1;

    private int mode = EXPLORE_MODE;

    /**
     * Instantiates a new Shrink dfs.
     *
     * @param model the model
     */
    ShrinkDfs( final Model model ) {
      this.model = model;
      this.network = model.getNetwork();
    }

    /**
     * Init.
     *
     * @param entryPointNode the entry point node
     */
    public void init( final Object entryPointNode ) {

      this.entryPointNode = entryPointNode;

      round = 0;
      if ( instanceMap == null ) {
        this.instanceMap = new HashMap<>();
      }
      Iterator nodeIterator = network.nodes();
      while (nodeIterator.hasNext()) {
        Object n = nodeIterator.next();
        instanceMap.put( n, -1 );
      }
    }

    /**
     * Next round int.
     *
     * @return the int
     */
    protected int nextRound() {
      round++;
      numSkipped = 0;
      numInstantiated = 0;
      super.start( network, entryPointNode );
      return numInstantiated;
    }

    @Override
    protected void postVisit( final Object node, final int i, final int j ) {

      if ( mode == EXPLORE_MODE ) {
        if ( NodeType.isNewNode( model.getNodeType( node ) ) ) {

          final Object classNode = model.getClassNode( node );

          instanceMap.put( classNode, round );
          numInstantiated++;
        }
      }
    }

    @Override
    protected void preVisit( final Object node, final int dfsNumber ) {

      if ( mode == RESULT_MODE ) {

        model.markNotObsolete( node );
      }
    }

    /**
     * Mark reachable nodes.
     */
    protected void markReachableNodes() {
      int oldMode = mode;
      mode = RESULT_MODE;
      super.start( network, this.entryPointNode );
      mode = oldMode;
    }

    @Override
    protected boolean doTraverse( final Object edge ) {

      boolean allowed = false;

      // TODO use NodeType
      final Object target = network.getTarget(edge);
      
      // class, field node: allow always
      if ( !NodeType.isMethodNode( model.getNodeType( target ) ) ) {

        allowed = true;
      } else {

        if ( ! (model.getDependencyType( edge ).equals( EdgeType.RESOLVE ) ||
                model.getDependencyType( edge ).equals( EdgeType.ENCLOSE ))) {

          final AbstractDescriptor targetDescriptor = model.getDescriptor( target );
          final MethodDescriptor targetMethod = (MethodDescriptor) targetDescriptor;
          final Object classNode = model.getClassNode( target );
          final ClassDescriptor targetClass = (ClassDescriptor) model.getDescriptor( classNode );

          allowed = allowed || targetMethod.isStatic();

          // default method
          allowed = allowed || targetMethod.hasFlag(Opcodes.ACC_PUBLIC) && !targetMethod.hasFlag(Opcodes.ACC_ABSTRACT);

          allowed = allowed || targetClass.isAnnotation();

          allowed = allowed || ( model.getDependencyType( edge ).equals( EdgeType.SUPER ) );

          allowed = allowed ||
              ( NodeType.isNewNode( model.getNodeType( target ) ) );

          allowed = allowed || ( Model.CONSTRUCTOR_NAME.equals( targetMethod.getName() ) );

          allowed = allowed || ( targetMethod.isPrivate() );

          allowed = allowed || wasClassInstantiated( edge );

          allowed = allowed || isMethodNeeded( targetClass, targetMethod );

          allowed = allowed || model.getDependencyType( edge ).equals( EdgeType.INVOKEDYNAMIC );

          // resolve edge: mark target stub as needed
        } else if ( mode == RESULT_MODE ) {
          model.markStubNeeded( target );
        }
      }

      return allowed;
    }

    /**
     * A Method is needed if a descendant class does not override the method and the descendant class is instantiated.
     * TODO improve alorithm
     */
    private boolean isMethodNeeded( ClassDescriptor cd, MethodDescriptor md ) {

      List<ClassDescriptor> descendants = new ArrayList<ClassDescriptor>( 5 );
      model.getInternalDescendants( cd, descendants );

      for ( ClassDescriptor descendant : descendants ) {

        if ( ( !descendant.implementsMethod( md.getName(), md.getDesc() ) ) &&
          (int) instanceMap.get( descendant.getNode() ) >= ( round - 1 ) ) {
          return true;
        }
      }
      return false;
    }

    private boolean wasClassInstantiated( final Object edge ) {

      final Object targetNode = network.getTarget(edge);
      final Object classNode = model.getClassNode( targetNode );
      if ( (int) instanceMap.get( classNode ) >= ( round - 1 ) ) {
        return true;
      } else {

        numSkipped++;

        return false;
      }
    }

    /**
     * Gets num skipped.
     *
     * @return the num skipped
     */
    protected int getNumSkipped() {
      return numSkipped;
    }
  }
}
