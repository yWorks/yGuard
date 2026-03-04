package com.yworks.yshrink;

import com.yworks.common.ShrinkBag;
import com.yworks.yshrink.ant.filters.AllMainMethodsFilter;
import com.yworks.yshrink.ant.filters.EntryPointFilter;
import com.yworks.yshrink.core.Analyzer;
import com.yworks.yshrink.core.ClassResolver;
import com.yworks.yshrink.core.Writer;
import com.yworks.yshrink.core.Shrinker;
import com.yworks.yshrink.model.AbstractDescriptor;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.logging.ConsoleLogger;
import com.yworks.logging.Logger;
import com.yworks.yshrink.util.Util;
import com.yworks.logging.XmlLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * How the Shrinker works:
 * - Initially, no node is marked as instantiated and all nodes are marked as obsolete.
 * - Using the global entrypoint-node as the startnode, a dfs is run on the dependency graph.
 * - Edges are traversed under the following conditions:
 * - If the target node is a class or field node, the edge may always be traversed.
 * - An edge to a method node may be traversed if it is not a RESOLVE edge and:
 * - The target method is a invoke dynamic or
 * - The target method is static or
 * - The edge represents a super call or
 * - The target node is the NEW-node of a class or
 * - The target method is a constructor or
 * - The target method is private or
 * - The target method is a regular method and the class the method belongs to is marked as instantiated or
 * - The target method is needed, i.e. a child class of the class the method belongs to is instantiated and does not override the method.
 * - If an edge is allowed to be traversed and the target node is a NEW-node, the corresponding class is marked as instantiated.
 * - As long as the amount of classes marked as instantiated increases, rerun the dfs.
 * - If the amount of classes marked as instantiated does not increase between two dfs-rounds, a last dfs is run in order to mark all reachable nodes as non-obsolete. Also, the RESOLVE edges that target methods are allowed to be traversed in this last round in order to mark the target methods as needed for resolving (that is, only a stub of these methods is needed).
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class YShrink {

  private static final String CENTER_CLASS = "";//"y/view/NodeLabel";
  private static final String CENTER_METHOD_NAME = "";//"setOffsetDirty";
  private static final String CENTER_METHOD_DESC = "";

  //private boolean showGraph = false;
  private final boolean createStubs;

  private String digests;

  /**
   * Instantiates a new Y shrink.
   */
  public YShrink() {
    this.createStubs = true;
  }

  /**
   * Instantiates a new Y shrink.
   *
   * @param createStubs the create stubs
   * @param digests     the digests
   */
  public YShrink( boolean createStubs, String digests ) {
    this.createStubs = createStubs;
    this.digests = digests;
  }

  /**
   * Basic steps
   * - Init model: create nodes for each class, method and field. Additionally, create a single entrypoint-node and one NEW-node for each class.
   * - Mark all entrypoints: using a composite EntryPointFilter, mark/log every entrypoint-node in the model.
   * - Create all dependency edges using the com.yworks.yshrink.core.Analyzer.
   * - Create additional entrypoint-edges between the entrypoint-node and each entrypoint that doesn't represent an ordinary method (non-abstract,non-static,non-constructor).
   * - Mark all obsolete classes, methods and fields using the com.yworks.yshrink.core.Shrinker.
   * - Write out all non-obsolete classes using the com.yworks.yshrink.core.Writer.
   *
   * @param pairs    the pairs
   * @param epf      the epf
   * @param resolver the resolver
   * @throws IOException the io exception
   */
  public void doShrinkPairs( List<ShrinkBag> pairs, EntryPointFilter epf, ClassResolver resolver ) throws
      IOException {

    final Analyzer analyzer = new Analyzer();

    Model model = new Model();

    if ( null != resolver ) model.setClassResolver( resolver );

    // create nodes
    if ( ! model.isSimpleModelSet() ) {
      analyzer.initModel( model, pairs );
    }

    // mark entrypoints
    List<AbstractDescriptor> entryPoints = markEntryPoints( model, epf );

    // create edges
    if ( model.isSimpleModelSet() ) {
      analyzer.createDependencyEdges( model );
    } else {
      analyzer.createEdges( model );
    }
    model.createEntryPointEdges( entryPoints );

    final Shrinker shrinker = new Shrinker();
    shrinker.shrink( model );

    final Writer writer = new Writer(createStubs, digests );

    for ( ShrinkBag bag : pairs ) {
      if ( ! bag.isEntryPointJar() ) {
        writer.write( model, bag );
      }
    }

    if ( !model.isAllResolved() ) {
      Logger.warn( "Not all dependencies could be resolved. Please see the logfile for details." );
    }
  }

  /**
   * If a constructor is an entrypoint, the synthetic new-node of its class is also marked as an entrypoint.
   *
   * 
		 * @param model
   * 
		 * @param epFilter
   */
  private List<AbstractDescriptor> markEntryPoints( final Model model,
                                                    final EntryPointFilter epFilter ) {

    StringBuilder buf = new StringBuilder();
    buf.append( "<entrypoints>\n" );

    final List<AbstractDescriptor> entryPoints = new ArrayList<AbstractDescriptor>();

    for ( ClassDescriptor cd : model.getAllClassDescriptors() ) {

      epFilter.setRetainAttribute( cd );

      if ( epFilter.isEntryPointClass( model, cd ) ) {
        buf.append( "\t<class name=\"" );
        buf.append( Util.toJavaClass( cd.getName() ) );
        buf.append( "\" />\n" );
        entryPoints.add( cd );
        cd.setEntryPoint( true );
        model.markNotObsolete( cd.getNode() );
      }
      for ( MethodDescriptor md : cd.getMethods() ) {
        if ( epFilter.isEntryPointMethod( model, cd, md ) ) {
          buf.append(
              "\t<method signature=\"" );
          buf.append( XmlLogger.replaceSpecialChars( md.getSignature() ) );
          buf.append( "\" class=\"" );
          buf.append( Util.toJavaClass(
              cd.getName() ) );
          buf.append( "\" />\n" );

          entryPoints.add( md );
          md.setEntryPoint( true );

          if ( md.getName().equals( Model.CONSTRUCTOR_NAME ) ) {
            AbstractDescriptor newNodeDesc = model.getDescriptor( cd.getNewNode() );
            if ( ! newNodeDesc.isEntryPoint() ) {
              newNodeDesc.setEntryPoint( true );
              entryPoints.add( newNodeDesc );
            }
          }
        }
      }
      for ( FieldDescriptor fd : cd.getFields() ) {
        if ( epFilter.isEntryPointField( model, cd, fd ) ) {
          buf.append( "\t<field name=\"" );
          buf.append( Util.toJavaClass( fd.getName() ) );
          buf.append( "\" class=\"" );
          buf.append( Util.toJavaClass( cd.getName() ) );
          buf.append( "\" />\n" );
          entryPoints.add( fd );
          fd.setEntryPoint( true );
        }
      }
    }

    buf.append( "</entrypoints>\n" );
    Logger.shrinkLog( buf.toString() );

    return entryPoints;
  }

  /**
   * Main.
   *
   * @param args the args
   */
  public static void main( final String[] args ) {

    new ConsoleLogger();

    try {

      boolean showGraph = false;

      File in = null;
      File out = null;

      if ( args.length > 0 ) {
        in = new File( args[ 0 ] );
      }

      if ( null == in ) {
        in = new File( ClassLoader.getSystemResource( "yshrink.jar" ).getFile() );
      }

      if ( args.length > 1 ) {
        out = new File( args[ 1 ] );
      }

      if ( null == out ) {
        out = new File( System.getProperty( "user.dir" ) + "/out.jar" );
      }

      if ( args.length > 2 ) {
        showGraph = Boolean.parseBoolean( args[ 2 ] );
      }

      final URL[] externalLibs = new URL[]{
          //ClassLoader.getSystemResource( "asm-2.2.2.jar" ),
          //ClassLoader.getSystemResource( "y.jar" ),
          ClassLoader.getSystemResource( "external.jar" )
      };

      final YShrink yshrink = new YShrink();

      final EntryPointFilter epf = new AllMainMethodsFilter();

    } catch ( Exception e ) {
      Logger.err( "An Exception occured.", e );
    }
  }
}
