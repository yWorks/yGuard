package com.yworks.yshrink.model;

import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import com.yworks.yshrink.core.ClassResolver;
import com.yworks.yshrink.util.Logger;
import com.yworks.yshrink.util.Util;
import com.yworks.util.graph.Node;
import com.yworks.util.graph.Edge;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import com.google.common.graph.MutableNetwork;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Model {

  Map<String, ClassDescriptor> model;

  protected MutableNetwork<Node, Edge> network;
  private Map<Object, Object> dependencyTypes;
  protected Map<Object, Object> node2Descriptor;
  protected Map<Object, Object> node2Type;

  private Node entryPointNode;

  private boolean simpleModelSet = false;

  private ClassResolver resolver;

  private boolean allResolved = true;

  public static String VOID_DESC = Type.getMethodDescriptor( Type.VOID_TYPE, new Type[0] );
  public static final String CONSTRUCTOR_NAME = "<init>";
  //public static final String SYNTHETIC_NEW_NODE_NAME = "NEW";

  public void setClassResolver( final ClassResolver res ) {
    if ( res != null ) {
      resolver = res;
    } else {
      resolver = new DefaultClassResolver();
    }
  }

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

  public List<MethodDescriptor> getAllConstructors( final ClassDescriptor cd ) {

    final List<MethodDescriptor> constructors = new ArrayList<MethodDescriptor>();

    for ( MethodDescriptor md : cd.getMethods() ) {
      if ( CONSTRUCTOR_NAME.equals( md.getName() ) ) {
        constructors.add( md );
      }
    }

    return constructors;
  }

  public boolean isSimpleModelSet() {
    return simpleModelSet;
  }

  public Model() {
    this( null );
  }

  public Model( MutableNetwork<Node, Edge> network ) {
    if ( network != null ) {
      this.network = network;
    } else {
      this.network = NetworkBuilder
              .directed()
              .allowsParallelEdges(true)
              .allowsSelfLoops(true)
              .build();
    }

    setClassResolver( null );
    node2Descriptor = new HashMap<>();
    node2Type = new HashMap<>();
    dependencyTypes = new HashMap<>();
    model = new HashMap<>();

    entryPointNode = new Node(this.network);
    this.network.addNode(entryPointNode);
    node2Type.put( entryPointNode, NodeType.ENTRYPOINT );
  }

  public Node getEntryPointNode() {
    return entryPointNode;
  }

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
  public Edge createDependencyEdge( final AbstractDescriptor source, final AbstractDescriptor target,
                                    final EdgeType type ) {

    if ( ( !source.equals( target ) ) ) {
      return createDependencyEdge( source.getNode(), target.getNode(), type );
    } else {
      return null;
    }
  }

  public Edge createDependencyEdge( final Node sourceNode, final Node targetNode, final EdgeType edgeType ) {
    if ( hasEdge( sourceNode, targetNode, edgeType ) ) {
      return null;
    } else {
      final Edge e = new Edge(this.network);
      this.network.addEdge( sourceNode, targetNode, e );
      dependencyTypes.put( e, edgeType );
      return e;
    }
  }

  private boolean hasEdge( final Node src, final Node tgt, final EdgeType type ) {
    for (Edge edge : network.edgesConnecting(src, tgt)) {
      if (dependencyTypes.get(edge) == type) {
        return true;
      }
    }
    return false;
  }

  // TODO merge these 
  public ClassDescriptor newClassDescriptor( final String name, final int access, final File sourceJar ) {

    final Node newNode = new Node(this.network);
    this.network.addNode(newNode);
    final AbstractDescriptor newNodeDescriptor = new NewNodeDescriptor( Opcodes.ACC_PUBLIC, sourceJar );
    node2Descriptor.put( newNode, newNodeDescriptor );
    node2Type.put( newNode, NodeType.NEW );
    newNodeDescriptor.setNode( newNode );

    final ClassDescriptor cd = new ClassDescriptor( name, access, newNode, sourceJar );

    final Node classNode = new Node(this.network);
    this.network.addNode(classNode);
    node2Descriptor.put( classNode, cd );
    node2Type.put( classNode, NodeType.CLASS );
    cd.setNode( classNode );
    model.put( name, cd );

    return cd;
  }

  public ClassDescriptor newClassDescriptor( final String name, final String superName, final String[] interfaces,
                                             final int access, final File sourceJar ) {

    final Node newNode = new Node(this.network);
    this.network.addNode(newNode);
    final AbstractDescriptor newNodeDescriptor = new NewNodeDescriptor( Opcodes.ACC_PUBLIC, sourceJar );
    node2Descriptor.put( newNode, newNodeDescriptor );
    node2Type.put( newNode, NodeType.NEW );
    newNodeDescriptor.setNode( newNode );

    final ClassDescriptor cd = new ClassDescriptor( name, superName, interfaces, access, newNode, sourceJar );

    final Node classNode = new Node(this.network);
    this.network.addNode(classNode);
    node2Descriptor.put( classNode, cd );
    node2Type.put( classNode, NodeType.CLASS );
    cd.setNode( classNode );
    model.put( name, cd );

    return cd;
  }

  public MethodDescriptor newMethodDescriptor( final ClassDescriptor cd, final int access, final String name,
                                               final String desc,
                                               final String[] exceptions, final File sourceJar ) {

    final MethodDescriptor md = new MethodDescriptor( name, access, desc, exceptions, sourceJar );
    cd.addMethod( md );
    final Node n = new Node(this.network);
    this.network.addNode(n);
    node2Descriptor.put( n, md );
    node2Type.put( n, NodeType.METHOD );
    md.setNode( n );
    return md;
  }

  public FieldDescriptor newFieldDescriptor( final ClassDescriptor cd, final String desc, final String name,
                                             final int access, final File sourceJar ) {
    final FieldDescriptor fd = new FieldDescriptor( desc, name, access, sourceJar );
    cd.addField( fd );
    final Node n = new Node(this.network);
    network.addNode(n);
    node2Descriptor.put( n, fd );
    node2Type.put( n, NodeType.FIELD );
    fd.setNode( n );

    return fd;
  }

  public Collection<ClassDescriptor> getAllClassDescriptors() {
    return model.values();
  }

  public Collection<String> getAllClassNames() {
    return model.keySet();
  }

  public ClassDescriptor getClassDescriptor( final String className ) {
    if ( isClassModeled( className ) ) {
      return model.get( className );
    } else {
      return null;
    }
  }

  public AbstractDescriptor getDescriptor( final Node n ) {
    return (AbstractDescriptor) node2Descriptor.get( n );
  }

  public Node getClassNode( final Node memberNode ) {

    if ( getDescriptor( memberNode ) instanceof ClassDescriptor ) {
      throw new IllegalArgumentException( "Node " + memberNode + " is a classNode " );
    }

    for (final Edge e: memberNode.outEdges() ) {
      if ( getDependencyType( e ).equals( EdgeType.MEMBER_OF ) ) {
        return e.target();
      }
    }

    throw new RuntimeException( "Node " + memberNode + " is homeless." );
  }

  public EdgeType getDependencyType( final Edge e ) {
    return (EdgeType) dependencyTypes.get( e );
  }

  /**
   * retrieve all implementing classes of <code>cd</code>.
   *
   * @param cd
   * @return List of ClassDescriptors containing all classes that implement cd
   */
  public Set<ClassDescriptor> getAllImplementingClasses( final ClassDescriptor cd ) {
    Set<ClassDescriptor> ret = null;

    for (final Edge e: cd.getNode().inEdges()) {
      if ( dependencyTypes.get( e ).equals( EdgeType.IMPLEMENTS ) ) {
        if ( ret == null ) ret = new HashSet<ClassDescriptor>();
        final ClassDescriptor subClass = (ClassDescriptor) node2Descriptor.get( e.source() );
        ret.add( subClass );
      }
    }

    return ret;
  }

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

  public void getAllInternalAncestorEntrypointMethods( final String className,
                                                       final List<MethodDescriptor> methods ) {

    if ( null == className || ! isClassModeled( className ) ) {
      return;
    }

    ClassDescriptor cd = getClassDescriptor( className );

    Collection<MethodDescriptor> classMethods = cd.getMethods();
    for ( MethodDescriptor md : classMethods ) {
      if ( md.isEntryPoint() ) {
        methods.add( md );
      }
    }

    if ( ! ( cd.isInterface() && "java/lang/Object".equals( cd.getSuperName() ) ) ) {
      getAllInternalAncestorEntrypointMethods( cd.getSuperName(), methods );
    }
    String[] interfaces = cd.getInterfaces();
    for ( String interfc : interfaces ) {
      getAllInternalAncestorEntrypointMethods( interfc, methods );
    }
  }


  public boolean getAllExternalAncestorMethods( final String className, final List<Method> methods ) {

    boolean r = true;

    if ( null == className ) {
      return true;
    }

    if ( isClassModeled( className ) ) {

      ClassDescriptor cd = getClassDescriptor( className );
      if ( ! ( cd.isInterface() && "java/lang/Object".equals( cd.getSuperName() ) ) ) {
        r &= getAllExternalAncestorMethods( cd.getSuperName(), methods );
      }
      String[] interfaces = cd.getInterfaces();
      for ( String interfc : interfaces ) {
        r &= getAllExternalAncestorMethods( interfc, methods );
      }
    } else {

      Class clazz = resolve( className );

      if ( null != clazz ) {

        // add all methods
        Method[] clazzMethods = clazz.getDeclaredMethods();

        for ( Method method : clazzMethods ) {
          methods.add( method );
        }

        // collect superclass methods
        Class superClass = clazz.getSuperclass();
        if ( null != superClass ) {
          r &= getAllExternalAncestorMethods( superClass.getName(), methods );
        }

        // collect interface methods
        Class[] interfaces = clazz.getInterfaces();

        for ( Class interfc : interfaces ) {
          r &= getAllExternalAncestorMethods( interfc.getName(), methods );
        }
      } else {
        return false;
      }
    }
    return r;
  }

  public boolean getAllInternalAncestorMethods( String className, final List<MethodDescriptor> methods ) {

    boolean r = true;

    if ( null == className ) {
      return true;
    }

    if ( isClassModeled( className ) ) {

      ClassDescriptor cd = getClassDescriptor( className );

      for ( MethodDescriptor methodDescriptor : cd.getMethods() ) {
        methods.add( methodDescriptor );
      }

      if ( ! ( cd.isInterface() && "java/lang/Object".equals( cd.getSuperName() ) ) ) {
        r &= getAllInternalAncestorMethods( cd.getSuperName(), methods );
      }
      String[] interfaces = cd.getInterfaces();
      for ( String interfc : interfaces ) {
        r &= getAllInternalAncestorMethods( interfc, methods );
      }
    } else {

      Class clazz = resolve( className );

      if ( null != clazz ) {

        // add all methods
        Method[] clazzMethods = clazz.getDeclaredMethods();

        // collect superclass methods
        Class superClass = clazz.getSuperclass();
        if ( null != superClass ) {
          r &= getAllInternalAncestorMethods( superClass.getName(), methods );
        }

        // collect interface methods
        Class[] interfaces = clazz.getInterfaces();

        for ( Class interfc : interfaces ) {
          r &= getAllInternalAncestorMethods( interfc.getName(), methods );
        }
      } else {
        return false;
      }
    }
    return r;
  }

  /**
   * collects all subclasses of <code>cd</code>
   *
   * @param cd
   * @param descendants
   */
  public void getInternalDescendants( final ClassDescriptor cd, final List<ClassDescriptor> descendants ) {
    for ( final Edge e: cd.getNode().inEdges() ) {
      if ( dependencyTypes.get( e ).equals( EdgeType.EXTENDS ) ) {
        final ClassDescriptor subClass = (ClassDescriptor) node2Descriptor.get( e.source() );
        descendants.add( subClass );
        getInternalDescendants( subClass, descendants );
      }
    }
  }

  /**
   * determine wether class <code>className</code> has any ancestor classes/interfaces that cannot be resolved.
   *
   * @param className the class name
   * @return true iff any ancestor classes of <code>className</code> are not contained in this model and cannot be
   *         resolved using the given <code>resolver</code>.
   */
  public boolean hasUnresolvableAncestors( final String className ) {

    boolean r = false;
    if ( isClassModeled( className ) ) {
      final ClassDescriptor cd = getClassDescriptor( className );
      r = r || hasUnresolvableAncestors( cd.getSuperName() );
      for ( String interfc : cd.getInterfaces() ) {
        r = r || hasUnresolvableAncestors( interfc );
      }
    } else {
      resolve( className );
    }

    return r;
  }

  private boolean isMethodDefinedInExternalInterface( final ClassDescriptor origClass, final MethodDescriptor md ) {

    boolean found = false;

    String[] interfaces = origClass.getInterfaces();
    for ( String interfc : interfaces ) {
      if ( ! isClassModeled( interfc ) ) {
        Class clazz = resolve( interfc );
        if ( null != clazz ) {
          found = found || containsNonPrivateMethod( clazz, md );
          for ( Class clazzz : clazz.getInterfaces() ) {
            found = found || isMethodDefinedInExternalInterfaceRec( clazzz, md );
          }
        }
      }
    }

    return found;
  }

  private boolean isMethodDefinedInExternalInterfaceRec( final Class clazz, final MethodDescriptor md ) {

    boolean found = false;
    if ( containsNonPrivateMethod( clazz, md ) ) {
      return true;
    }
    for ( Class clazzz : clazz.getInterfaces() ) {
      found = found || isMethodDefinedInExternalInterfaceRec( clazzz, md );
    }
    return found;
  }

  /**
   * determine wether the method <code>md</code> is implemented in any superclasses of class <code>className</code> or
   * if <code>md</code> is declared in any interface that class <code>className</code> or any superclass of class
   * <code>className</code> implements.
   *
   * @param origClass
   * @param md
   * @return true iff an implementation or declaration of <code>md</code> is found in any ancestor class/interface of
   *         class <code>className</code>
   */
  public boolean isMethodExternallyDefined( final ClassDescriptor origClass,
                                            final MethodDescriptor md ) {

    boolean found = false;

    found = found || isMethodExternallyDefinedRec( origClass.getSuperName(), md );

    for ( String interfc : origClass.getInterfaces() ) {
      found = found || isMethodExternallyDefinedRec( interfc, md );
    }

    List<ClassDescriptor> descendants = new ArrayList<ClassDescriptor>();
    getInternalDescendants( origClass, descendants );
    for ( ClassDescriptor cd : descendants ) {
      found = found || isMethodDefinedInExternalInterface( cd, md );
    }

    return found;
  }

  /**
   * TODO ugly d:[ determine wether <code>md</code> is implemented or declared in <code>className</code> or any of class
   * <code>className</code>s ancestor classes/interfaces.
   *
   * @param className
   * @param md
   * @return true iff an implementation or declaration of <code>md</code> is found in any ancestor class/interface of
   *         class <code>className</code>
   */
  private boolean isMethodExternallyDefinedRec( final String className,
                                                final MethodDescriptor md ) {

    boolean found = false;

    if ( isClassModeled( className ) ) {

      final ClassDescriptor cd = getClassDescriptor( className );

      boolean internallyDefined = false;
//      for ( MethodDescriptor cdMd : cd.getMethods() ) {
//        if ( md.overrides( cdMd ) ) internallyDefined = true;
//      }

      if ( !internallyDefined ) {

        final String[] interfaces = cd.getInterfaces();
        final String superClass = cd.getSuperName();

        if ( interfaces.length > 0 ) {
          for ( int i = 0; i < interfaces.length; i++ ) {
            found = found || isMethodExternallyDefinedRec( interfaces[ i ], md );
          }
        }

        if ( className != "java/lang/Object" ) { // we might be analyzing rt.jar :]
          found = found || isMethodExternallyDefinedRec( superClass, md );
        }
      }
    } else {

      Class clazz = resolve( className );

      if ( clazz != null ) {
        // ALL methods, including inherited.

        found = containsNonPrivateMethod( clazz, md );

        if ( !found && !"java/lang/Object".equals( clazz.getName() ) ) {

          final Class superClass = clazz.getSuperclass();
          if ( superClass != null ) {
            found = found || isMethodExternallyDefinedRec( superClass.getName(), md );
          }

          final Class[] interfaces = clazz.getInterfaces();

          for ( Class interfc : interfaces ) {
            found = found || isMethodExternallyDefinedRec( interfc.getName(), md );
          }
        }
      }
    }

    return found;
  }

  private boolean containsNonPrivateMethod( Class clazz, MethodDescriptor md ) {

    boolean found = false;
    final Method[] methods = clazz.getDeclaredMethods();

    for ( int i = 0; i < methods.length; i++ ) {
      //final MethodDescriptor md2 = method2Descriptor( methods[ i ] );
      if ( !Modifier.isPrivate( methods[ i ].getModifiers() ) ) {
        if ( md.overrides( methods[ i ] ) ) found = true;
      }
    }
    return found;
  }

  public void createEntryPointEdges( List<AbstractDescriptor> entryPoints ) {

    Node entryPointNode = getEntryPointNode();

    for ( AbstractDescriptor descriptor : entryPoints ) {
      if ( descriptor instanceof MethodDescriptor ) {

        MethodDescriptor md = (MethodDescriptor) descriptor;

        createDependencyEdge( entryPointNode, md.getNode(), EdgeType.INVOKES );
        createDependencyEdge( entryPointNode, md.getNode(), EdgeType.RESOLVE );
      } else {
        createDependencyEdge( entryPointNode, descriptor.getNode(), EdgeType.ENTRYPOINT );
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

  public boolean isAllResolved() {
    return allResolved;
  }

  /**
   * convert java.lang.reflect.Method to MethodDescriptor
   *
   * @param m java Method
   * @return a MethodDescriptor with the same properties as Method <code>m</code>.
   */
  private MethodDescriptor method2Descriptor( final Method m, final File sourceJar ) {

    final int access = m.getModifiers();
    final String desc = Type.getMethodDescriptor( m );
    final Class[] exceptionClasses = m.getExceptionTypes();
    final String[] exceptions = new String[ exceptionClasses.length ];
    for ( int i = 0; i < exceptionClasses.length; i++ ) {
      exceptions[ i ] = exceptionClasses[ i ].getName();
    }

    return new MethodDescriptor( m.getName(), access, desc, exceptions, sourceJar );
  }

  public int getNodeType( final AbstractDescriptor ad ) {
    return (Integer) node2Type.get( ad.getNode() );
  }

  public int getNodeType( final Node n ) {
    return (int) node2Type.get( n );
  }

  public void markObsolete( final Node n ) {

    int type = getNodeType( n );
    if ( ! NodeType.isObsolete( type ) ) {
      type += NodeType.OBSOLETE;
      node2Type.put( n, type );
    }
  }

  public void markNotObsolete( final Node n ) {

    // TODO use ~
    int type = getNodeType( n );
    if ( NodeType.isObsolete( type ) ) {
      type -= NodeType.OBSOLETE;
      node2Type.put( n, type );
    }
  }

  public void markStubNeeded( final Node n ) {
    int type = getNodeType( n );
    if ( ! NodeType.isStubNeeded( type ) ) {
      type += NodeType.STUB;
      node2Type.put( n, type );
    }
  }

  public boolean isObsolete( final Node n ) {
    return NodeType.isObsolete( (int) node2Type.get( n ) );
  }

  public boolean isStubNeeded( final Node n ) {
    return NodeType.isStubNeeded( (int) node2Type.get( n ) );
  }

  public Network<Node, Edge> getNetwork() {
    return network;
  }
}
