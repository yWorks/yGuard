package com.yworks.yshrink.model;

import com.yworks.util.graph.DefaultNetwork;
import com.yworks.logging.Logger;
import com.yworks.yshrink.core.ClassResolver;
import com.yworks.yshrink.util.MultiReleaseException;
import com.yworks.yshrink.util.Util;
import com.yworks.util.graph.Network;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Model.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Model {

  /**
   * The Model.
   */
  Map<String, ClassDescriptor> model;

  /**
   * The Network.
   */
  protected Network network;
  private Map<Object, Object> dependencyTypes;
  /**
   * The Node 2 descriptor.
   */
  protected Map<Object, Object> node2Descriptor;
  /**
   * The Node 2 type.
   */
  protected Map<Object, Object> node2Type;

  private Object entryPointNode;

  private boolean simpleModelSet = false;

  private ClassResolver resolver;

  private boolean allResolved = true;

  /**
   * The constant VOID_DESC.
   */
  public static String VOID_DESC = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[0] );
  /**
   * The constant CONSTRUCTOR_NAME.
   */
  public static final String CONSTRUCTOR_NAME = "<init>";
  //public static final String SYNTHETIC_NEW_NODE_NAME = "NEW";

  /**
   * Sets class resolver.
   *
   * @param res the res
   */
  public void setClassResolver( final ClassResolver res ) {
    if ( res != null ) {
      resolver = res;
    } else {
      resolver = new DefaultClassResolver();
    }
  }

  /**
   * Sets simple model set.
   */
  public void setSimpleModelSet() {
    this.simpleModelSet = true;
  }

  private static class DefaultClassResolver implements ClassResolver {
    public Class resolve( final String className ) throws ClassNotFoundException {
      return Class.forName( className, false, getClass().getClassLoader() );
    }

    @Override
    public void close() throws Exception {}
  }

  /**
   * Is simple model set boolean.
   *
   * @return the boolean
   */
  public boolean isSimpleModelSet() {
    return simpleModelSet;
  }

  /**
   * Instantiates a new Model.
   */
  public Model() {
    this( null );
  }

  /**
   * Instantiates a new Model.
   *
   * @param network the network
   */
  public Model( Network network ) {
    if ( network != null ) {
      this.network = network;
    } else {
      this.network = new DefaultNetwork();
    }

    setClassResolver( null );
    node2Descriptor = new HashMap<>();
    node2Type = new HashMap<>();
    dependencyTypes = new HashMap<>();
    model = new HashMap<>();

    entryPointNode = this.network.createNode();
    node2Type.put( entryPointNode, NodeType.ENTRYPOINT );
  }

  /**
   * Is class modeled boolean.
   *
   * @param className the class name
   * @return the boolean
   */
  public boolean isClassModeled( final String className ) {
    return model.containsKey( className );
  }

  /**
   * creates a dependency of type <code>type</code> iff no edge of the same type exists between <code>source</code> and
   * <code>target</code>.
   *
   * @param source the edge source
   * @param target the edge target
   * @param type   the edge type
   * @return the created edge, or null if no edge was created
   */
  public Object createDependencyEdge( final AbstractDescriptor source, final AbstractDescriptor target,
                                    final EdgeType type ) {

    if ( ( !source.equals( target ) ) ) {
      return createDependencyEdge( source.getNode(), target.getNode(), type );
    } else {
      return null;
    }
  }

  /**
   * Create dependency edge edge.
   *
   * @param sourceNode the source node
   * @param targetNode the target node
   * @param edgeType   the edge type
   * @return the edge
   */
  public Object createDependencyEdge( final Object sourceNode, final Object targetNode, final EdgeType edgeType ) {
    if ( hasEdge( sourceNode, targetNode, edgeType ) ) {
      return null;
    } else {
      final Object e = network.createEdge(sourceNode, targetNode);
      dependencyTypes.put( e, edgeType );
      return e;
    }
  }

  private boolean hasEdge( final Object src, final Object tgt, final EdgeType type ) {
    Iterator connectingEdgesIterator = network.edgesConnecting(src, tgt);
    while (connectingEdgesIterator.hasNext()) {
      Object currentEdge = connectingEdgesIterator.next();
      if (dependencyTypes.get(currentEdge) == type) {
        return true;
      }
    }
    return false;
  }

  /**
   * New class descriptor class descriptor.
   *
   * @param name       the name
   * @param superName  the super name
   * @param interfaces the interfaces
   * @param access     the access
   * @param sourceJar  the source jar
   * @return the class descriptor
   */
  public ClassDescriptor newClassDescriptor( final String name, final String superName, final String[] interfaces,
                                             final int access, final File sourceJar ) {

    final Object newNode = network.createNode();
    final AbstractDescriptor newNodeDescriptor = new NewNodeDescriptor( Opcodes.ACC_PUBLIC, sourceJar );
    node2Descriptor.put( newNode, newNodeDescriptor );
    node2Type.put( newNode, NodeType.NEW );
    newNodeDescriptor.setNode( newNode );

    final ClassDescriptor cd = new ClassDescriptor( name, superName, interfaces, access, newNode, sourceJar );

    final Object classNode = network.createNode();
    node2Descriptor.put( classNode, cd );
    node2Type.put( classNode, NodeType.CLASS );
    cd.setNode( classNode );
    model.put( name, cd );

    return cd;
  }

  /**
   * New method descriptor method descriptor.
   *
   * @param cd         the cd
   * @param access     the access
   * @param name       the name
   * @param desc       the desc
   * @param exceptions the exceptions
   * @param sourceJar  the source jar
   * @return the method descriptor
   */
  public MethodDescriptor newMethodDescriptor( final ClassDescriptor cd, final int access, final String name,
                                               final String desc,
                                               final String[] exceptions, final File sourceJar ) {

    final MethodDescriptor md = new MethodDescriptor( name, access, desc, exceptions, sourceJar );
    cd.addMethod( md );
    final Object n = network.createNode();
    node2Descriptor.put( n, md );
    node2Type.put( n, NodeType.METHOD );
    md.setNode( n );
    return md;
  }

  /**
   * New field descriptor field descriptor.
   *
   * @param cd        the cd
   * @param desc      the desc
   * @param name      the name
   * @param access    the access
   * @param sourceJar the source jar
   * @return the field descriptor
   */
  public FieldDescriptor newFieldDescriptor( final ClassDescriptor cd, final String desc, final String name,
                                             final int access, final File sourceJar ) {
    final FieldDescriptor fd = new FieldDescriptor( desc, name, access, sourceJar );
    cd.addField( fd );
    final Object n = network.createNode();
    node2Descriptor.put( n, fd );
    node2Type.put( n, NodeType.FIELD );
    fd.setNode( n );

    return fd;
  }

  /**
   * Gets all class descriptors.
   *
   * @return the all class descriptors
   */
  public Collection<ClassDescriptor> getAllClassDescriptors() {
    return model.values();
  }

  /**
   * Gets all class names.
   *
   * @return the all class names
   */
  public Collection<String> getAllClassNames() {
    return model.keySet();
  }

  /**
   * Gets class descriptor.
   *
   * @param className the class name
   * @return the class descriptor
   */
  public ClassDescriptor getClassDescriptor( final String className ) {
    if ( isClassModeled( className ) ) {
      return model.get( className );
    } else {
      if (className.startsWith("META-INF")) {
        throw new MultiReleaseException();
      }
      return null;
    }
  }

  /**
   * Gets all implemented interfaces.
   *
   * @param className  the class name
   * @param interfaces the interfaces
   */
  public void getAllImplementedInterfaces( final String className, final Set<String> interfaces ) {

    if ( "java/lang/Object".equals( className ) ) {
      return;
    }

    if ( isClassModeled( className ) ) {
      ClassDescriptor cd = getClassDescriptor( className );
      String[] cInterfaces = cd.getInterfaces();
      interfaces.addAll( Arrays.asList( cInterfaces ) );
      for ( String interfc : cInterfaces ) {
        getAllImplementedInterfaces( interfc, interfaces );
      }
      getAllImplementedInterfaces( cd.getSuperName(), interfaces );
    } else {
      Class clazz = resolve( className );

      if ( null != clazz ) {
        Class[] cInterfaces = clazz.getInterfaces();
        for ( Class cInterface : cInterfaces ) {
          String internalClassName = Util.toInternalClass(cInterface.getName());
          interfaces.add(internalClassName);
          getAllImplementedInterfaces(internalClassName, interfaces);
        }
        Class superclass = clazz.getSuperclass();
        if (superclass != null) {
          getAllImplementedInterfaces(Util.toInternalClass(superclass.getName()), interfaces);
        }
      }
    }
  }

  /**
   * Gets all ancestor classes.
   *
   * @param className the class name
   * @param parents   the parents
   */
  public void getAllAncestorClasses( final String className,
                                     final Set<String> parents ) {

    if ( "java/lang/Object".equals( className ) ) {
      return;
    }

    if ( isClassModeled( className ) ) {
      String superName = getClassDescriptor( className ).getSuperName();
      parents.add( superName );
      getAllAncestorClasses( superName, parents );
    } else {
      Class clazz = resolve( className );
      if ( null != clazz ) {
        Class superclass = clazz.getSuperclass();
        if ( null != superclass ) { // else: Object, Interface..
          String superName = Util.toInternalClass(superclass.getName());
          parents.add( superName );
          getAllAncestorClasses( superName, parents );
        }
      }
    }
  }

  private Class resolve( String className ) {
    Class clazz = null;
    try {
      clazz = resolver.resolve( Util.toJavaClass( className ) );
    } catch ( ClassNotFoundException e ) {
      Logger.warnToLog( "Unresolved external dependency: " + Util.toJavaClass( className ) + " not found!" );
      allResolved = false;
    } catch ( RuntimeException e ) {
      Logger.warnToLog( "error resolving class " + className );
      allResolved = false;
    } finally {
      return clazz;
    }
  }
}
