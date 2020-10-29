package com.yworks.yshrink.model;

import com.yworks.util.graph.DefaultNetwork;
import com.yworks.logging.Logger;
import com.yworks.yshrink.core.ClassResolver;
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
   * Gets entry point node.
   *
   * @return the entry point node
   */
  public Object getEntryPointNode() {
    return entryPointNode;
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
      return null;
    }
  }

  /**
   * Gets descriptor.
   *
   * @param n the n
   * @return the descriptor
   */
  public AbstractDescriptor getDescriptor( final Object n ) {
    return (AbstractDescriptor) node2Descriptor.get( n );
  }

  /**
   * Gets class node.
   *
   * @param memberNode the member node
   * @return the class node
   */
  public Object getClassNode( final Object memberNode ) {

    if ( getDescriptor( memberNode ) instanceof ClassDescriptor ) {
      throw new IllegalArgumentException( "Node " + memberNode + " is a classNode " );
    }

    Iterator outEdgesIterator = network.outEdges(memberNode);
    while (outEdgesIterator.hasNext()) {
      Object e = outEdgesIterator.next();
      if ( getDependencyType( e ).equals( EdgeType.MEMBER_OF ) ) {
        return network.getTarget(e);
      }
    }

    throw new RuntimeException( "Node " + memberNode + " is homeless." );
  }

  /**
   * Gets dependency type.
   *
   * @param e the e
   * @return the dependency type
   */
  public EdgeType getDependencyType( final Object e ) {
    return (EdgeType) dependencyTypes.get( e );
  }

  /**
   * retrieve all implementing classes of <code>cd</code>.
   *
   * @param cd the cd
   * @return List of ClassDescriptors containing all classes that implement cd
   */
  public Set<ClassDescriptor> getAllImplementingClasses( final ClassDescriptor cd ) {
    Set<ClassDescriptor> ret = null;

    Iterator inEdgesIterator = network.inEdges(cd.getNode());
    while (inEdgesIterator.hasNext()) {
      Object e = inEdgesIterator.next();
      if ( dependencyTypes.get( e ).equals( EdgeType.IMPLEMENTS ) ) {
        if ( ret == null ) ret = new HashSet<ClassDescriptor>();
        final ClassDescriptor subClass = (ClassDescriptor) node2Descriptor.get( network.getSource(e) );
        ret.add( subClass );
      }
    }

    return ret;
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

  /**
   * Gets all internal ancestor entrypoint methods.
   *
   * @param className the class name
   * @param methods   the methods
   */
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


  /**
   * Gets all external ancestor methods.
   *
   * @param className the class name
   * @param methods   the methods
   * @return the all external ancestor methods
   */
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

  /**
   * collects all subclasses of <code>cd</code>
   *
   * @param cd          the cd
   * @param descendants the descendants
   */
  public void getInternalDescendants( final ClassDescriptor cd, final List<ClassDescriptor> descendants ) {
    Iterator inEdgesIterator = network.inEdges(cd.getNode());
    while (inEdgesIterator.hasNext()) {
      Object e = inEdgesIterator.next();
      if ( dependencyTypes.get( e ).equals( EdgeType.EXTENDS ) ) {
        final ClassDescriptor subClass = (ClassDescriptor) node2Descriptor.get( network.getSource(e) );
        descendants.add( subClass );
        getInternalDescendants( subClass, descendants );
      }
    }
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
   * @param origClass the orig class
   * @param md        the md
   * @return true iff an implementation or declaration of <code>md</code> is found in any ancestor class/interface of         class <code>className</code>
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
   *
		 * @param className
   *
		 * @param md
   * 
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

  /**
   * Create entry point edges.
   *
   * @param entryPoints the entry points
   */
  public void createEntryPointEdges( List<AbstractDescriptor> entryPoints ) {

    Object entryPointNode = getEntryPointNode();

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

  /**
   * Is all resolved boolean.
   *
   * @return the boolean
   */
  public boolean isAllResolved() {
    return allResolved;
  }

  /**
   * convert java.lang.reflect.Method to MethodDescriptor
   *
   *
		 * @param m java Method
   * 
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

  /**
   * Gets node type.
   *
   * @param n the n
   * @return the node type
   */
  public int getNodeType( final Object n ) {
    return (int) node2Type.get( n );
  }

  /**
   * Mark obsolete.
   *
   * @param n the n
   */
  public void markObsolete( final Object n ) {

    int type = getNodeType( n );
    if ( ! NodeType.isObsolete( type ) ) {
      type += NodeType.OBSOLETE;
      node2Type.put( n, type );
    }
  }

  /**
   * Mark not obsolete.
   *
   * @param n the n
   */
  public void markNotObsolete( final Object n ) {

    // TODO use ~
    int type = getNodeType( n );
    if ( NodeType.isObsolete( type ) ) {
      type -= NodeType.OBSOLETE;
      node2Type.put( n, type );
    }
  }

  /**
   * Mark stub needed.
   *
   * @param n the n
   */
  public void markStubNeeded( final Object n ) {
    int type = getNodeType( n );
    if ( ! NodeType.isStubNeeded( type ) ) {
      type += NodeType.STUB;
      node2Type.put( n, type );
    }
  }

  /**
   * Is obsolete boolean.
   *
   * @param n the n
   * @return the boolean
   */
  public boolean isObsolete( final Object n ) {
    return NodeType.isObsolete( (int) node2Type.get( n ) );
  }

  /**
   * Is stub needed boolean.
   *
   * @param n the n
   * @return the boolean
   */
  public boolean isStubNeeded( final Object n ) {
    return NodeType.isStubNeeded( (int) node2Type.get( n ) );
  }

  /**
   * Gets network.
   *
   * @return the network
   */
  public Network getNetwork() {
    return network;
  }
}
