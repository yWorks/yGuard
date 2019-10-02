package com.yworks.yshrink;

import com.yworks.yguard.common.ShrinkBag;
import com.yworks.yshrink.ant.filters.AllMainMethodsFilter;
import com.yworks.yshrink.ant.filters.EntryPointFilter;
import com.yworks.yshrink.core.Analyzer;
import com.yworks.yshrink.core.ClassResolver;
import com.yworks.yshrink.core.Shrinker;
import com.yworks.yshrink.core.Writer;
import com.yworks.yshrink.model.AbstractDescriptor;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.util.ConsoleLogger;
import com.yworks.yshrink.util.Logger;
import com.yworks.yshrink.util.Util;
import com.yworks.yshrink.util.XmlLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class YShrink {

  private static final String CENTER_CLASS = "";//"y/view/NodeLabel";
  private static final String CENTER_METHOD_NAME = "";//"setOffsetDirty";
  private static final String CENTER_METHOD_DESC = "";

  //private boolean showGraph = false;
  private final boolean createStubs;

  private String digests;

  public YShrink() {
    this.createStubs = true;
  }

  public YShrink( boolean createStubs, String digests ) {
    this.createStubs = createStubs;
    this.digests = digests;
  }

  public void doShrinkPairs( List<ShrinkBag> pairs, EntryPointFilter epf, ClassResolver resolver ) throws
      IOException {

    final Analyzer analyzer = new Analyzer();
//    NavigationView nv = null;
//    if ( showGraph ) {
//      nv = new NavigationView();
//    }
    //Model model = ( showGraph ) ? new Model2D( nv.getGraph() ) : new Model();

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

    final Writer writer = new Writer( createStubs, digests );

    for ( ShrinkBag bag : pairs ) {
      if ( ! bag.isEntryPointJar() ) {
        writer.write( model, bag );
      }
    }

    if ( !model.isAllResolved() ) {
      Logger.warn( "Not all dependencies could be resolved. Please see the logfile for details." );
    }

//    if ( null != nv ) {
//
//      ClassDescriptor cd = model.getClassDescriptor( CENTER_CLASS );
//
//      AbstractDescriptor centerDesc = null;
//
//      if ( ! "".equals( CENTER_METHOD_DESC ) && ! "".equals( CENTER_METHOD_NAME ) ) {
//        centerDesc = cd.getMethod( CENTER_METHOD_NAME, CENTER_METHOD_DESC );
//      }
//      if ( centerDesc == null ) {
//        centerDesc = cd;
//      }
//      if ( centerDesc != null ) {
//        nv.show( centerDesc.getNode() );
//      } else {
//        System.out.println( "VIEW: given class/method not modeled." );
//        nv.show( model.getGraph().firstNode() );
//      }
//    }
  }

//  public void doShrinkPairs( final URL[] inFiles, final URL[] outFiles, final EntryPointFilter epf,
//                             final ClassResolver resolver, Writer.ResourcePolicy[] resources ) throws ShrinkException {
//
//    final Analyzer analyzer = new Analyzer();
//    NavigationView nv = null;
//    if ( showGraph ) {
//       nv = new NavigationView();
//    }
//    Model model = ( showGraph ) ? new Model2D( nv.getGraph() ) : new Model();
//    if( null != resolver ) model.setClassResolver( resolver );
//
//    try {
//
//      // create nodes
//      analyzer.initModel( model, inFiles );
//
//      // mark entrypoints
//      List<AbstractDescriptor> entryPoints = markEntryPoints( model, epf );
//
//      // create edges
//      analyzer.createEdges( model );
//
//      model.createEntryPointEdges( entryPoints );
//
//      final Shrinker shrinker = new Shrinker();
//      shrinker.shrink( model );
//
//      final Writer writer = new Writer( createStubs, digests );
//
//      for ( int i = 0; i < inFiles.length; i++ ) {
//        writer.write( model, inFiles[ i ], outFiles[ i ], resources[ i ] );
//      }
//
//      if ( null != nv ) {
//
//        ClassDescriptor cd = model.getClassDescriptor( CENTER_CLASS );
//
//        AbstractDescriptor centerDesc = null;
//
//        if ( ! "".equals( CENTER_METHOD_DESC ) && ! "".equals( CENTER_METHOD_NAME ) ) {
//          centerDesc = cd.getMethod( CENTER_METHOD_NAME, CENTER_METHOD_DESC );
//        }
//        if ( centerDesc == null ) {
//          centerDesc = cd;
//        }
//        if ( centerDesc != null ) {
//          nv.show( centerDesc.getNode() );
//        } else {
//          System.out.println( "VIEW: given class/method not modeled." );
//          nv.show( model.getGraph().firstNode() );
//        }
//      }
//    } catch ( IOException e ) {
//      throw new ShrinkException( "An IOException occured.", e.getCause() );
//    }
//  }

//  private void doShrinkPair( final File inFile, final File outFile, final EntryPointFilter epf,
//                             final URL[] externalClasses,
//                             final boolean showGraph, Writer.ResourcePolicy resourcePolicy ) throws ShrinkException {
//
//    this.showGraph = showGraph;
//    final URLCpResolver resolver = new URLCpResolver( externalClasses );
//    try {
//      doShrinkPairs( new URL[]{ inFile.toURL() }, new URL[]{ outFile.toURL() }, epf, resolver,
//          new Writer.ResourcePolicy[] { resourcePolicy });
//    } catch ( MalformedURLException e ) {
//      e.printStackTrace(); //TODO handle
//    }
//  }

  /**
   * If a constructor is an entrypoint, the synthetic new-node of its class is also marked as an entrypoint.
   *
   * @param model
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

//      try {
//        yshrink.doShrinkPair( in, out, epf, externalLibs, showGraph, Writer.ResourcePolicy.COPY );
//      } catch ( ShrinkException e ) {
//        e.printStackTrace(); //TODO handle
//      }

//      URL[] inFiles = new URL[] { ClassLoader.getSystemResource( "yshrink.jar" ), new File("/afs/yworks.home/home/schroede/lib/asm-2.2.2/lib/asm-2.2.2.jar").toURL() };
//      URL[] outFiles = new URL[] { new File(System.getProperty( "user.dir" ) + "/yshrink-out.jar").toURL(),   new File(System.getProperty( "user.dir" ) + "/asm-out.jar").toURL() };
//
//      yshrink.doShrinkPair( inFiles, outFiles, epf, null );

    } catch ( Exception e ) {
      Logger.err( "An Exception occured.", e );
    }
  }
}
