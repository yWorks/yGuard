package com.yworks.yshrink.core;

import com.yworks.util.abstractjar.impl.DirectoryStreamProvider;
import com.yworks.util.graph.Node;
import com.yworks.common.ShrinkBag;
import com.yworks.yshrink.model.AbstractDescriptor;
import com.yworks.yshrink.model.AnnotationUsage;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.EdgeType;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.Invocation;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.model.ModelVisitor;
import com.yworks.util.abstractjar.impl.JarStreamProvider;
import com.yworks.yshrink.util.Logger;
import com.yworks.util.abstractjar.StreamProvider;
import com.yworks.yshrink.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Analyzer {

  private static final String SYNTHETIC_DOT_CLASS_FIELD_START = "class$";
  private static final String CLASS_DESC = "Ljava/lang/Class;";

  public void createEdges( Model model ) {
    createInheritanceEdges( model );
    createDependencyEdges( model );
  }

  /**
   * Create all nodes needed for dependency analysis using a <code>ModelVisitor</code>. Also creates artificial
   * &lt;clinit&gt; nodes for each (non-inner) class if not already present.
   *
   * @param model
   * @param bags
   * @throws IOException
   */
  public void initModel( Model model, List<ShrinkBag> bags ) throws IOException {

    for ( ShrinkBag bag : bags ) {
      ModelVisitor mv = new ModelVisitor( model, bag.getIn() );
      Logger.log( "parsing " + bag.getIn() );
      visitAllClasses( mv, bag.getIn() );
    }

    for ( ClassDescriptor cd : model.getAllClassDescriptors() ) {

      // add static initializers if not present
      MethodDescriptor clinit = cd.getMethod( "<clinit>", Model.VOID_DESC );

      if ( ! cd.isInnerClass() ) {

        if ( clinit == null ) {
          clinit = model.newMethodDescriptor( cd, Opcodes.ACC_STATIC, "<clinit>", Model.VOID_DESC, null,
              cd.getSourceJar() );
        }

      }
      if (null != clinit) {
        // make sure to create an edge to existing clinit methods, inner classes *can* contain static
        // initializers if created synthetically (e.g. enum tableswitch)
        model.createDependencyEdge( cd, clinit, EdgeType.INVOKES );
      }

      createEnumEdges(model,cd);

    }
  }

  /**
   * Let <code>v</code> visit all classes contained in <code>jarFile</code>
   *
   * @param v
   * @param jarFile
   * @throws IOException
   */
  private void visitAllClasses( final ClassVisitor v, final File jarFile ) throws IOException {

    final StreamProvider jarStreamProvider = (jarFile.isDirectory()) ? new DirectoryStreamProvider(jarFile) : new JarStreamProvider(jarFile);
    InputStream stream = jarStreamProvider.getNextClassEntryStream();
    ClassReader cr;
    while ( stream != null ) {
      cr = new ClassReader( stream );

      // asm 3.1
      cr.accept( v, 0 );

      // asm 2.2.2
      //cr.accept( v, false );

      stream = jarStreamProvider.getNextClassEntryStream();
    }
  }

  /**
   * create EXTENDS / IMPLEMENTS edges.
   *
   * @param model
   */
  public void createInheritanceEdges( final Model model ) {

    for ( ClassDescriptor cm : model.getAllClassDescriptors() ) {

      // EXTENDS
      if ( model.isClassModeled( cm.getSuperName() ) ) {
        final ClassDescriptor superDescriptor = model.getClassDescriptor( cm.getSuperName() );
        model.createDependencyEdge( cm, superDescriptor, EdgeType.EXTENDS );
      }

      // IMPLEMENTS
      for ( String interfc : cm.getInterfaces() ) {
        if ( model.isClassModeled( interfc ) ) {
          final ClassDescriptor superDescriptor = model.getClassDescriptor( interfc );
          model.createDependencyEdge( cm, superDescriptor, EdgeType.IMPLEMENTS );
        }
      }
    }

    model.setSimpleModelSet();
  }

  /**
   * Create all kinds of dependency edges for the whole <code>model</code>.
   *
   * @param model
   */
  public void createDependencyEdges( final Model model ) {

    for ( ClassDescriptor cd : model.getAllClassDescriptors() ) {

      createAnnotationEdges(cd, model);

      model.createDependencyEdge( cd.getNewNode(), cd.getNode(), EdgeType.MEMBER_OF );

      createInnerClassEdges( model, cd );
      createAssumeEdges( model, cd );

      for ( MethodDescriptor md : cd.getMethods() ) {

        createAnnotationEdges(md, model);

        model.createDependencyEdge( md, cd, EdgeType.MEMBER_OF );

        createReferenceEdges( model, md );
        createMethodSignatureEdges( model, md );
        createInvokeEdges( model, cd, md );
        createTypeInstructionEdges( model, md );
      }
      for ( FieldDescriptor fd : cd.getFields() ) {
        createAnnotationEdges(fd, model);
        model.createDependencyEdge( fd, cd, EdgeType.MEMBER_OF );

        // resolve edge for field type
        // not required for verification, but obfuscator will complain if the type is not found.
        String fieldTypeName = Util.getTypeNameFromDescriptor(fd.getDesc());
        if (model.isClassModeled(fieldTypeName)) {
          ClassDescriptor fieldType = model.getClassDescriptor(fieldTypeName);
          model.createDependencyEdge(fd, fieldType, EdgeType.RESOLVE);
        }

      }
    }
  }

  private void createAnnotationEdges(AbstractDescriptor cd, Model model) {
    for (AnnotationUsage annotationUsage : cd.getAnnotations()) {
      if (model.isClassModeled(annotationUsage.getDescriptor())) {
        ClassDescriptor annotationClassDescriptor = model.getClassDescriptor(annotationUsage.getDescriptor());
        model.createDependencyEdge(cd, annotationClassDescriptor, EdgeType.REFERENCES);

        for (String field : annotationUsage.getFieldUsages()) {
          for (MethodDescriptor methodDescriptor : annotationClassDescriptor.getMethods()) {
            if (methodDescriptor.getName().equals(field)) {
              model.createDependencyEdge(cd, methodDescriptor, EdgeType.RESOLVE);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Create RESOLVE edges from <code>md</code> to the runtime type of type instructions ANEWARRAY, MULTIANEWARRAY,
   * INSTANCEOF, CHECKCAST and LDC (if class version &gt= 49.0). Create CREATES edges for NEW instructions from
   * <code>md</code> to the NEW-node of the given runtime type.
   *
   * @param model
   * @param md
   */
  private void createTypeInstructionEdges( final Model model, final MethodDescriptor md ) {
    for ( AbstractMap.SimpleEntry<Object, Object> typeInstruction : md.getTypeInstructions() ) {
      final int opcode = (Integer) typeInstruction.getKey();
      final String desc = (String) typeInstruction.getValue();

      final String type = Util.getTypeNameFromDescriptor( desc );

      if ( opcode == Opcodes.ANEWARRAY
          || opcode == Opcodes.MULTIANEWARRAY
          || opcode == Opcodes.INSTANCEOF
          || opcode == Opcodes.CHECKCAST
          || opcode == Opcodes.LDC ) // .class, version >= 49.0
      {

        if ( model.isClassModeled( type ) ) {
          ClassDescriptor cd = model.getClassDescriptor( type );
          model.createDependencyEdge( md, cd, EdgeType.RESOLVE );
        }
      } else if ( opcode == Opcodes.NEW ) {

        if ( model.isClassModeled( type ) ) {
          ClassDescriptor targetClass = model.getClassDescriptor( type );
          model.createDependencyEdge( md.getNode(), targetClass.getNewNode(), EdgeType.CREATES );
        }
      }
    }
  }

  private void createEnumEdges( final Model model, final ClassDescriptor cd ) {


    Set parents = new HashSet();
    model.getAllAncestorClasses( cd.getName(), parents );

    if( parents.contains("java/lang/Enum") ) {

      String enumName = cd.getName();
      Type enumType = Type.getType(Util.verboseToNativeType(enumName));
      Type stringType = Type.getType(Util.verboseToNativeType("java/lang/String"));
      Type enumArray = Type.getType(Util.verboseToNativeType(enumName + "[]" ) );

      Collection<MethodDescriptor> methods = cd.getMethods();
      for( MethodDescriptor method : methods ) {
        boolean isStatic = method.isStatic();
        Type retval = method.getReturnType();
        String name = method.getName();
        Type[] args = method.getArgumentTypes();

        // public static test.simple.EnumTest$FontType valueOf(java.lang.String);
        if( isStatic
            && retval.equals(enumType)
            && "valueOf".equals(name)
            && args.length == 1
            && args[0].equals(stringType)) {
          model.createDependencyEdge( cd,  method, EdgeType.INVOKES );
        } else
           // public static test.simple.EnumTest$FontType[] values();
          if( isStatic &&
              args.length == 0 &&
              "values".equals(name) &&
              retval.equals(enumArray) ) {
            model.createDependencyEdge( cd,  method, EdgeType.INVOKES );
          }
      }
    }
  }

  /**
   * create ASSUME edges: dependencies from the NEW-node of <code>cd</code> to methods of class <code>cd</code>
   * implementing/overriding methods of external interfaces/classes. If any ancestor class/interface of <code>cd</code>
   * cannot be resolved, all non-private methods are assumed to be called. Also create ASSUME edges from the NEW-node of
   * <code>cd</code> to all non-static, non-constructor methods that are marked as entrypoints and that are implemented
   * in <code>cd</code> or an ancestor class of <code>cd</code>.
   *
   * @param model
   * @param cd
   */
  private void createAssumeEdges( final Model model, final ClassDescriptor cd ) {

    if ( cd.isInterface() ) {
      return;
    }

    Node newNode = cd.getNewNode();

    if ( newNode == null ) {
      Logger.err( "no NEW-Node found for " + cd.getName() );
      return;
    }

    List<Method> externalMethods = new ArrayList<Method>( 5 );

    boolean resolvable = model.getAllExternalAncestorMethods( cd.getName(), externalMethods );

    if ( resolvable ) {
      for ( Method method : externalMethods ) {
        String mName = method.getName();
        String mDesc = Type.getMethodDescriptor( method );

        if ( cd.implementsMethod( mName, mDesc ) ) {

          model.createDependencyEdge( newNode, cd.getMethod( mName, mDesc ).getNode(), EdgeType.ASSUME );
        } else {
          List<ClassDescriptor> modeledClasses = new ArrayList<>();
          for ( String interfaceName: cd.getInterfaces() ) {
            if ( model.isClassModeled( interfaceName ) ) modeledClasses.add( model.getClassDescriptor( interfaceName ) );
          }
          if ( model.isClassModeled( cd.getSuperName() ) ) modeledClasses.add( model.getClassDescriptor( cd.getSuperName() ) );

          for ( ClassDescriptor superDescriptor: modeledClasses ) {
            createEdgeToImplementingMethod( superDescriptor, mName, mDesc, model, newNode, EdgeType.ASSUME, false );
          }
        }
      }
    } else {  // assume all non-private methods are called.
      for ( MethodDescriptor md : cd.getMethods() ) {
        if ( ! md.isPrivate() ) {
          model.createDependencyEdge( newNode, md.getNode(), EdgeType.ASSUME );
        }
      }
    }

    List<MethodDescriptor> internalMethods = new ArrayList<MethodDescriptor>();
    model.getAllInternalAncestorEntrypointMethods( cd.getName(), internalMethods );

    for ( MethodDescriptor md : internalMethods ) {

      String mName = md.getName();
      String mDesc = md.getDesc();

      if ( !md.isStatic() || mName.equals( Model.CONSTRUCTOR_NAME ) ) {

        if ( cd.implementsMethod( mName, mDesc ) ) {
          model.createDependencyEdge( newNode, cd.getMethod( mName, mDesc ).getNode(), EdgeType.ASSUME );
        } else {
          if ( model.isClassModeled( cd.getSuperName() ) ) {
            ClassDescriptor superCd = model.getClassDescriptor( cd.getSuperName() );

            createEdgeToImplementingMethod( superCd, mName, mDesc, model, newNode, EdgeType.ASSUME, false );
          }
        }
      }
    }
  }

  /**
   * create INVOKES edges: For all method invocations <i>mi</i> in method <code>md</code>:
   * <ul>
   *  <li><i>mi</i> is constructor:
   *    <ul>
   *      <li><i>mi</i> is first invocation in <code>md</code>, <code>md</code> is constructor as well:
   *          create CHAIN dependency from <code>md</code> to <i>mi</i>.</li>
   *      <li>else: add CREATES dependency from
   *          <code>md</code> to <i>mi</i>.
   *      </ul>
   *    </li> <li>else: see documentation of createEdgeToImplementingMethod,
   * createSubtreeEdges </ul>
   *
   * @param model
   * @param cd
   * @param md
   */
  private void createInvokeEdges( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {

    for ( Invocation invocation : md.getInvocations() ) {

      final int opcode = invocation.getOpcode();
      final String targetType = invocation.getType();
      final String targetMethod = invocation.getName();
      final String targetDesc = invocation.getDesc();

      if ( model.isClassModeled( targetType ) ) { // else: external class (ignored)

        ClassDescriptor target = model.getClassDescriptor( targetType );

        // super calls: CHAIN and SUPER edges.
        if ( opcode == Opcodes.INVOKESPECIAL &&
            targetType.equals( cd.getSuperName() ) ) {
          // "CHAIN" calls to super constructor
          if ( Model.CONSTRUCTOR_NAME.equals( targetMethod ) &&
              Model.CONSTRUCTOR_NAME.equals( md.getName() ) ) {

            final MethodDescriptor initMethod = target.getMethod( targetMethod, targetDesc );

            model.createDependencyEdge( md,
                initMethod,
                EdgeType.CHAIN );
          } else { // calls to super-methods

            while ( ! target.implementsMethod( targetMethod, targetDesc ) &&
                model.isClassModeled( target.getSuperName() ) ) {
              target = model.getClassDescriptor( target.getSuperName() );
            }

            if ( target.implementsMethod( targetMethod, targetDesc ) ) {
              model.createDependencyEdge( md,
                  target.getMethod( targetMethod, targetDesc ),
                  EdgeType.SUPER );
            }
          }
        } else {

          if ( target.isInterface() || target.isAbstract() ) {

//            ClassDescriptor temp = findDeclaringClass( model, target, targetMethod, targetDesc );
//            if ( temp != null ) {
//              target = temp;
//            }
            createEdgeToDeclaration( model, target, targetMethod, targetDesc, md );
          }

          // RULE 1.1.1
          createEdgesToAncestorMethods( model, target, md, targetMethod, targetDesc );

          if ( ! targetMethod.equals( Model.CONSTRUCTOR_NAME ) ) {
            // RULE 1.1.2
            createSubtreeEdges( model, cd, target, md, targetMethod, targetDesc );
          }
        }

        if ( opcode == Opcodes.INVOKEDYNAMIC ) {
          final MethodDescriptor initMethod = target.getMethod( targetMethod, targetDesc );
          model.createDependencyEdge(md, initMethod, EdgeType.INVOKEDYNAMIC);
        }
      }
    }
  }

  /**
   * Create a RESOLVE dependency from <code>source</code> to the first declaration of <code>targetMethod</code> found in
   * <code>target</code> or any ascendant abstract class or interface of <code>target</code>. (Although this method may
   * call itself recursively with a concrete class as <code>targetClass</code>, it should be called initially only with
   * interfaces or abstract classes as <code>targetClass</code>, since its purpose is to find a method declaration in
   * case the runtime type a method is called on is an abstract class or interface.
   *
   * @param model
   * @param targetClass
   * @param targetMethod
   * @param targetDesc
   * @param source
   */
  private void createEdgeToDeclaration( final Model model, ClassDescriptor targetClass,
                                        final String targetMethod,
                                        final String targetDesc, MethodDescriptor source ) {

    if ( targetClass.implementsMethod( targetMethod, targetDesc )
        && ( targetClass.isAbstract() || targetClass.isInterface() ) ) {
      model.createDependencyEdge( source, targetClass.getMethod( targetMethod, targetDesc ), EdgeType.RESOLVE );
      return;
    }

    String[] interfaces = targetClass.getInterfaces();
    if ( null != interfaces ) {

      for ( String interfc : interfaces ) {
        if ( model.isClassModeled( interfc ) ) {
          ClassDescriptor interfaceDesc = model.getClassDescriptor( interfc );
          createEdgeToDeclaration( model, interfaceDesc, targetMethod, targetDesc, source );
        }
      }
    }

    if ( !targetClass.isInterface() ) {
      String superName = targetClass.getSuperName();
      if ( model.isClassModeled( superName ) ) {
        ClassDescriptor superDesc = model.getClassDescriptor( superName );
        createEdgeToDeclaration( model, superDesc, targetMethod, targetDesc, source );
      }
    }
  }

  /**
   * <ul> <li><code>owner</code> is an interface: run this method on all concrete subclasses of class
   * <code>owner</code>, add INVOKE dependencies to all subclasses of all <code>owner</code>-implementations that
   * override <code>targetMethod</code>.</li> <li><code>owner</code> is a concrete class: add INVOKES dependency to the
   * implementation of <targetMethod> in class <code>owner</code> or any superclass of <code>owner</code>. While
   * searching for the implementation, add RESOLVE dependency to all visited classes.</li> </ul>
   * <p/>
   * <p/>
   * TODO think (interfaces)
   *
   * @param model
   * @param owner
   * @param md
   * @param targetMethod
   * @param targetDesc
   */
  private void createEdgesToAncestorMethods( final Model model, ClassDescriptor owner, final MethodDescriptor md,
                                             final String targetMethod, final String targetDesc ) {

    if ( owner.isInterface() ) {

      final Set<ClassDescriptor> implementingClasses = model.getAllImplementingClasses( owner );

      if ( implementingClasses != null ) {
        for ( ClassDescriptor ownerImpl : implementingClasses ) {
          createEdgesToAncestorMethods( model, ownerImpl, md, targetMethod, targetDesc );
          createSubtreeEdges( model, owner, ownerImpl, md, targetMethod, targetDesc );
        }
      }
    }

    createEdgeToImplementingMethod( owner, targetMethod, targetDesc, model, md, EdgeType.INVOKES, true );
  }

  private void createEdgeToImplementingMethod( ClassDescriptor owner, String targetMethod, String targetDesc,
                                               Model model, MethodDescriptor md,
                                               EdgeType type, boolean createResolveEdge ) {

    createEdgeToImplementingMethod( owner, targetMethod, targetDesc, model, md.getNode(), type, createResolveEdge );
  }

  /**
   * Finds all preceding interfaces relative to start.
   * @param start - the interface to start with
   * @param path - a initial path, should include the start element
   * @param paths - a empty list of paths that all paths will be appended to
   */
  private void findSuperInterfaces( Model model, ClassDescriptor start, List<ClassDescriptor> path, List<List<ClassDescriptor>> paths ) {
    path.add(start);

    boolean hasModeled = false;

    final int oldSize = path.size();
    for (String interfaceName : start.getInterfaces()) {
      if (model.isClassModeled(interfaceName)) {
        hasModeled = true;
        if (path.size() > oldSize) {
          path = new ArrayList<ClassDescriptor>(path.subList(0, oldSize));
        }
        findSuperInterfaces(model, model.getClassDescriptor(interfaceName), path, paths);
      }
    }

    if (!hasModeled) {
      paths.add(path);
    }
  }

  /**
   * create a dependency edge to a concrete implementation of <code>targetMethod</code> in <code>owner</code> or any
   * concrete superclass of <code>owner</code>.
   *
   * @param owner             the class which <code>targetMethod</code> was called on.
   * @param targetMethod
   * @param targetDesc
   * @param model
   * @param node              the source node of the dependency.
   * @param type              the EdgeType to use for the dependency edge.
   * @param createResolveEdge wether to create an additional RESOLVE edge.
   */
  private void createEdgeToImplementingMethod( ClassDescriptor owner, final String targetMethod, final String targetDesc,
                                               Model model, Node node,
                                               EdgeType type, boolean createResolveEdge ) {

    ArrayList<ClassDescriptor> classHierarchy = new ArrayList<ClassDescriptor>();
    classHierarchy.add(owner);
    while ( ! owner.implementsMethod( targetMethod, targetDesc ) &&
        model.isClassModeled( owner.getSuperName() ) ) {
      model.createDependencyEdge( node, owner.getNode(), EdgeType.RESOLVE );
      owner = model.getClassDescriptor( owner.getSuperName() );
      classHierarchy.add(owner);
    }
    if ( owner.implementsMethod( targetMethod, targetDesc ) ) {

      final MethodDescriptor targetMethodImp = owner.getMethod( targetMethod, targetDesc );

      model.createDependencyEdge( node, targetMethodImp.getNode(), type );
      // RESOLVE dependency needed since INVOKES-dependency edge might not be traversed if owner is not instantiated.
      if ( createResolveEdge && !owner.isInterface() ) {
        model.createDependencyEdge( node, targetMethodImp.getNode(), EdgeType.RESOLVE );
      }

      // static methods: RESOLVE dependency to implementing class
      if ( targetMethodImp.isStatic() ) {
        model.createDependencyEdge( node, owner.getNode(), EdgeType.RESOLVE );
      }
    // method is not implemented by any super class of owner, thus it must be a default method inherited from a interface
    } else {
      // gather all direct interfaces of the class and its super classes
      final HashSet<String> seen = new HashSet<String>();
      final ArrayList<ClassDescriptor> interfaceDescriptors = new ArrayList<ClassDescriptor>();
      if (owner.isInterface()) {
        seen.add(owner.getName());
        interfaceDescriptors.add(owner);
      }
      for (ClassDescriptor cd: classHierarchy) {
        for (String interfaceName : cd.getInterfaces()) {
          if (seen.add(interfaceName) && model.isClassModeled(interfaceName)) {
            interfaceDescriptors.add(model.getClassDescriptor(interfaceName));
          }
        }
      }

      // find all paths from all direct interfaces to their super interfaces
      final List<List<ClassDescriptor>> interfaceHierarchies = new ArrayList<List<ClassDescriptor>>();
      for (ClassDescriptor cd: interfaceDescriptors) {
        findSuperInterfaces(model, cd, new ArrayList<ClassDescriptor>(), interfaceHierarchies);
      }

      // determine the most specific interface implementation
      int mostSpecificDist = 0;
      ClassDescriptor mostSpecific = null;
      for (List<ClassDescriptor> hierarchy : interfaceHierarchies) {
        final int idx = indexOf(hierarchy, targetMethod, targetDesc);
        if (idx > -1) {
          final int dist = lastIndexOf(hierarchy, targetMethod, targetDesc) - idx + 1;
          if (mostSpecificDist < dist) {
            mostSpecificDist = dist;
            mostSpecific = hierarchy.get(idx);
          }
        }
      }

      if ( mostSpecific != null ) {
        final MethodDescriptor targetMethodImp = mostSpecific.getMethod( targetMethod, targetDesc );

        model.createDependencyEdge( node, targetMethodImp.getNode(), type );
        // RESOLVE dependency needed since INVOKES-dependency edge might not be traversed if owner is not instantiated.
        if ( createResolveEdge ) {
          model.createDependencyEdge( node, targetMethodImp.getNode(), EdgeType.RESOLVE );
        }

        // default methods: RESOLVE dependency to implementing interface
        if ( targetMethodImp.hasFlag(Opcodes.ACC_PUBLIC) && !targetMethodImp.hasFlag(Opcodes.ACC_ABSTRACT) ) {
          model.createDependencyEdge( node, owner.getNode(), EdgeType.RESOLVE );
        }
      }
    }
  }

  /**
   * Determines the index of the first class descriptor in the given list that
   * {@link ClassDescriptor#implementsMethod(String, String) implements} the
   * method identified by the given method name and method descriptor.
   * @return the index of the first class descriptor that implements the given
   * method or {@code -1} if there is no such descriptor in the given list.
   */
  private static int indexOf(
    final List<ClassDescriptor> interfaces,
    final String methodName, final String methodDescriptor
  ) {
    int idx = -1;
    for (ClassDescriptor cd : interfaces) {
      ++idx;
      if (cd.implementsMethod(methodName, methodDescriptor)) {
        return idx;
      }
    }
    return -1;
  }

  /**
   * Determines the index of the last class descriptor in the given list that
   * {@link ClassDescriptor#implementsMethod(String, String) implements} the
   * method identified by the given method name and method descriptor.
   * @return the index of the last class descriptor that implements the given
   * method or {@code -1} if there is no such descriptor in the given list.
   */
  private static int lastIndexOf(
    final List<ClassDescriptor> interfaces,
    final String methodName, final String methodDescriptor
  ) {
    int idx = interfaces.size();
    for (ListIterator<ClassDescriptor> it = interfaces.listIterator(idx); it.hasPrevious();) {
      --idx;
      final ClassDescriptor cd = it.previous();
      if (cd.implementsMethod(methodName, methodDescriptor)) {
        return idx;
      }
    }
    return -1;
  }

  /**
   * create INVOKES edges to all subclasses of class <code>cd</code> that override method <code>targetMethod</code>.
   *
   * @param model
   * @param cd
   * @param target
   * @param mm
   * @param targetMethod
   * @param targetDesc
   */
  private void createSubtreeEdges( final Model model, final ClassDescriptor cd, final ClassDescriptor target,
                                   final MethodDescriptor mm,
                                   final String targetMethod, final String targetDesc ) {

    final List<ClassDescriptor> subClasses = new ArrayList<ClassDescriptor>();
    model.getInternalDescendants( target, subClasses );

    for ( ClassDescriptor targetSubclass : subClasses ) {
      if ( targetSubclass != cd ) {
        if ( targetSubclass.implementsMethod( targetMethod, targetDesc ) ) {
          model.createDependencyEdge( mm, targetSubclass.getMethod( targetMethod, targetDesc ), EdgeType.INVOKES );
        }
      }
    }
  }

  /**
   * add RESOLVE dependency from <code>source</code> to the return type and all declared parameters and exceptions of
   * <code>source</code>.
   *
   * @param model
   * @param source
   */
  private void createMethodSignatureEdges( final Model model, final MethodDescriptor source ) {

    // arguments
    for ( Type argumentType : source.getArgumentTypes() ) {
      final String className = Util.getTypeNameFromDescriptor( argumentType.getDescriptor() );
      if ( model.isClassModeled( className ) ) {
        model.createDependencyEdge( source, model.getClassDescriptor( className ), EdgeType.RESOLVE );
      }
    }

    // return type
    final Type returnType = source.getReturnType();
    final String className = Util.getTypeNameFromDescriptor( returnType.getDescriptor() );
    if ( model.isClassModeled( className ) ) {
      model.createDependencyEdge( source, model.getClassDescriptor( className ), EdgeType.RESOLVE );
    }

    // Exceptions
    if ( source.getExceptions() != null ) {
      for ( String exception : source.getExceptions() ) {
        if ( model.isClassModeled( exception ) ) {
          final ClassDescriptor target = model.getClassDescriptor( exception );
          model.createDependencyEdge( source, target, EdgeType.RESOLVE );
        }
      }
    }
  }

  /**
   * if <code>cd</code> is an inner class, add ENCLOSE dependency to enclosing class or enclosing method.
   *
   * @param model
   * @param cd
   */
  private void createInnerClassEdges( final Model model, final ClassDescriptor cd ) {

    if ( cd.isInnerClass() ) {
      final ClassDescriptor enclosingClass = model.getClassDescriptor( cd.getEnclosingClass() );
      model.createDependencyEdge( cd, enclosingClass, EdgeType.ENCLOSE );
    }
    if ( cd.getEnclosingMethod() != null ) {
      final ClassDescriptor enclosingClass = model.getClassDescriptor( cd.getEnclosingClass() );
      MethodDescriptor enclosingMethodDescriptor = enclosingClass.getMethod(cd.getEnclosingMethod());
      if (null == enclosingMethodDescriptor) {
        Logger.log("Missing enclosing method declaration in "+enclosingClass.getName()+ ": "+cd.getEnclosingMethod().getValue());
      } else {
        model.createDependencyEdge( cd, enclosingMethodDescriptor, EdgeType.ENCLOSE );
      }
    }
  }

  /**
   * for all field references <i>f</i> in <code>md</code>, add REFERENCES dependency to the field declaration found in
   * the owner class/interface of <i>f</i> or any ancestor interface/class of the owner class. while searching for the
   * declaration of <i>f</i>, add RESOLVE dependency to all visited classes/interfaces.
   *
   * @param model
   * @param md
   */
  private void createReferenceEdges( final Model model, final MethodDescriptor md ) {

    for ( String[] fieldRef : md.getFieldRefs() ) {

      final String refDesc = fieldRef[ 0 ];
      final String refName = fieldRef[ 1 ];

      if ( model.isClassModeled( refDesc ) ) {

        ClassDescriptor owner = model.getClassDescriptor( refDesc );
        boolean declarationFound = owner.declaresField( refName );

        while ( model.isClassModeled( owner.getSuperName() ) && !declarationFound ) {
          if ( ! owner.declaresField( refName ) ) { // declared in interfaces?
            model.createDependencyEdge( md, owner, EdgeType.RESOLVE );

            for ( String interfc : owner.getInterfaces() ) {
              if ( model.isClassModeled( interfc ) ) {
                final ClassDescriptor interfcDesc = model.getClassDescriptor( interfc );
                if ( interfcDesc.declaresField( refName ) ) {
                  model.createDependencyEdge( md, interfcDesc.getField( refName ), EdgeType.REFERENCES );
                  declarationFound = true;
                }
              }
            }
          } else {
            declarationFound = true;
          }
          owner = model.getClassDescriptor( owner.getSuperName() );
        }

        if ( owner.declaresField( refName ) ) {

          model.createDependencyEdge( md, owner.getField( refName ), EdgeType.REFERENCES );
          checkLegacyDotClassField( refName, owner, model );
        }
      }
    }
  }

  private void checkLegacyDotClassField( String refName, ClassDescriptor owner, Model model ) {

    if ( refName.startsWith( SYNTHETIC_DOT_CLASS_FIELD_START ) ) {
      FieldDescriptor fd = owner.getField( refName );
      if ( CLASS_DESC.equals( fd.getDesc() ) && fd.isSynthetic() ) {

        StringBuilder[] possibleClassNames = getPossibleClassNames( refName );

        for ( StringBuilder possibleClassName : possibleClassNames ) {
          String className = possibleClassName.toString();
          if ( model.isClassModeled( className ) ) {
            ClassDescriptor cd = model.getClassDescriptor( className );
            model.createDependencyEdge( fd, cd, EdgeType.RESOLVE );
          }
        }
      }
    }
  }

  /**
   * finds all possible internal class names for a synthetic static field like e.g. <code>java.lang.Class
   * class$test$simple$F$FI</code> which is inserted in the bytecode of classes if the class version is &lt; 0.49 and
   * the <code>.class</code>-construct is used in the class.
   *
   * @param fieldName name of the synthetic field
   */
  private StringBuilder[] getPossibleClassNames( String fieldName ) {

    String[] toks = fieldName.substring( 6 ).split( "\\$" );

    StringBuilder[] possibleClassNames = new StringBuilder[ toks.length ];

    for ( int i = 0; i < possibleClassNames.length; i++ ) {
      possibleClassNames[ i ] = new StringBuilder();
      possibleClassNames[ i ].append( toks[ 0 ] );
    }

    for ( int i = 1; i < toks.length; i++ ) {
      for ( int j = i - 1; j >= 0; j-- ) {
        possibleClassNames[ j ].append( "$" ).append( toks[ i ] );
      }
      for ( int j = 0; j < i; j++ ) {
        possibleClassNames[ i ].append( "/" ).append( toks[ j + 1 ] );
      }
    }
    return possibleClassNames;
  }
}
