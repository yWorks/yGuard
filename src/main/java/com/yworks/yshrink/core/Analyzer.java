package com.yworks.yshrink.core;

import com.yworks.common.ShrinkBag;
import com.yworks.logging.Logger;
import com.yworks.util.abstractjar.Factory;
import com.yworks.util.abstractjar.StreamProvider;
import com.yworks.yshrink.model.AbstractDescriptor;
import com.yworks.yshrink.model.AnnotationUsage;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.EdgeType;
import com.yworks.yshrink.model.FieldDescriptor;
import com.yworks.yshrink.model.Invocation;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.model.ModelVisitor;
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
 * The type Analyzer.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class Analyzer {

  private static final String SYNTHETIC_DOT_CLASS_FIELD_START = "class$";
  private static final String CLASS_DESC = "Ljava/lang/Class;";

  /**
   * Create all nodes needed for dependency analysis using a <code>ModelVisitor</code>. Also creates artificial
   * &lt;clinit&gt; nodes for each (non-inner) class if not already present.
   *
   * @param model the model
   * @param bags  the bags
   * @throws IOException the io exception
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
   * @param v the visitor that will process the .class files in the given archive
   * @param jarFile an archive with .class files 
   * @throws IOException
   */
  private void visitAllClasses( final ClassVisitor v, final File jarFile ) throws IOException {

    final StreamProvider provider = Factory.newStreamProvider(jarFile);
    for (InputStream stream = provider.getNextClassEntryStream();
         stream != null;
         stream = provider.getNextClassEntryStream()) {
      final ClassReader cr = new ClassReader(stream );

      // asm 3.1
      cr.accept( v, 0 );

      // asm 2.2.2
      //cr.accept( v, false );

      close(stream);
    }
    try {
      provider.close();
    } catch (Exception ex) {
      // ignore
    }
  }

  /**
   * Closes the given stream.
   * @param is that will be closed.
   */
  private static void close( InputStream is ) {
    try {
      is.close();
    } catch (Exception ex) {
      // ignore
    }
  }

  /**
   * create EXTENDS / IMPLEMENTS edges.
   *
   * @param model the model
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
}
