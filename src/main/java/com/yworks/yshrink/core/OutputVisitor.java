package com.yworks.yshrink.core;

import com.yworks.yshrink.model.*;
import com.yworks.logging.Logger;
import com.yworks.yshrink.util.Util;
import com.yworks.logging.XmlLogger;
import com.yworks.yguard.obf.classfile.ClassConstants;
import org.objectweb.asm.*;

/**
 * The type Output visitor.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class OutputVisitor extends ClassVisitor {

  private ClassVisitor cv;
  private Model model;
  private final boolean createStubs;

  private int numObsoleteMethods = 0;
  private int numObsoleteFields = 0;

  private ClassDescriptor currentClass;

  private final DoNothingAnnotationVisitor ignoreAnnotation = new DoNothingAnnotationVisitor();

  /**
   * Instantiates a new Output visitor.
   *
   * @param cv          the cv
   * @param model       the model
   * @param createStubs the create stubs
   */
  public OutputVisitor( final ClassVisitor cv, final Model model, boolean createStubs ) {
    super(Opcodes.ASM7);
    this.createStubs = createStubs;
    this.cv = cv;
    this.model = model;
  }

  public void visit( final int version, final int access, final String name, final String signature,
                     final String superName, final String[] interfaces ) {

    currentClass = model.getClassDescriptor( name );
    if ( model.isObsolete( currentClass.getNode() ) ) {
      throw new IllegalArgumentException( "Writing obsolete class: " + name );
    }

    cv.visit( version, access, name, signature, superName, interfaces );
  }

  /**
   * @param source source file
   * @param debug  SourceDebugExtension
   */
  public void visitSource( String source, String debug ) {
    if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_SourceFile ) ) {
      source = null;
    }

    if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_SourceDebug ) ) {
      debug = null;
    }

    cv.visitSource( source, debug );
  }

  public void visitOuterClass( final String owner, final String name, final String desc ) {
    cv.visitOuterClass( owner, name, desc );
  }

  public AnnotationVisitor visitAnnotation( final String desc, final boolean visible ) {

    if ( visible ) {
      if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeVisibleAnnotations ) ) {
        return ignoreAnnotation;
      }
    } else {
      if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeInvisibleAnnotations ) ) {
        return ignoreAnnotation;
      }
    }

    return new OutputAnnotationVisitor( cv.visitAnnotation( desc, visible ) );

  }

  public AnnotationVisitor visitTypeAnnotation(
    final int typeRef, final TypePath typePath, final String descriptor, final boolean visible
  ) {
    if (visible) {
      if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeVisibleTypeAnnotations)) {
        return ignoreAnnotation;
      }
    } else {
      if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeInvisibleAnnotations)) {
        return ignoreAnnotation;
      }
    }
    return new OutputAnnotationVisitor(cv.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
  }

  public void visitAttribute( final Attribute attr ) {
    if ( currentClass.getRetainAttribute( attr.type ) ) {
      cv.visitAttribute( attr );
    }
  }

  public void visitInnerClass( final String name, final String outerName, final String innerName, final int access ) {
    // inner classes might be externally defined ??
    if ( model.isClassModeled( name ) ) {
      final ClassDescriptor cd = model.getClassDescriptor( name );
      if ( !model.isObsolete( cd.getNode() ) ) {
        cv.visitInnerClass( name, outerName, innerName, access );
      }
    }
  }

  public void visitNestHost( final String nestHost ) {
    final ClassDescriptor cd = model.getClassDescriptor(nestHost);
    if (!model.isObsolete(cd.getNode())) {
      cv.visitNestHost(nestHost);
    }
  }

  public void visitNestMember( final String nestMember ) {
    final ClassDescriptor cd = model.getClassDescriptor(nestMember);
    if (!model.isObsolete(cd.getNode()) && currentClass.getHasNestMembers()) {
      cv.visitNestMember(nestMember);
    }
  }

  public FieldVisitor visitField( final int access, final String name, final String desc, final String signature,
                                  final Object value ) {

    final FieldDescriptor fd = currentClass.getField( name );
    if ( model.isObsolete( fd.getNode() ) ) {
      Logger.shrinkLog(
              "\t\t<field name=\"" + name + "\" class=\"" + Util.toJavaClass( currentClass.getName() ) + "\" />" );
      numObsoleteFields++;
      return null;
    } else {
      return new OutputFieldVisitor(cv.visitField(access, name, desc, signature, value));
    }
  }

  public MethodVisitor visitMethod( final int access, final String name, final String desc, final String signature,
                                    final String[] exceptions ) {

    final MethodDescriptor md = currentClass.getMethod( name, desc );
    if ( model.isObsolete( md.getNode() ) ) {

      if ( ! model.isStubNeeded( md.getNode() ) ) {
        numObsoleteMethods++;
        Logger.shrinkLog( "\t\t<method signature=\"" +
                XmlLogger.replaceSpecialChars( md.getSignature() )
                + "\" class=\"" + Util.toJavaClass( currentClass.getName() ) + "\" />" );
      }

      if ( createStubs || model.isStubNeeded( md.getNode() ) ) {
        boolean visitStub = !md.hasFlag(Opcodes.ACC_ABSTRACT);
        return new StubOutputMethodVisitor( cv.visitMethod( access, name, desc, signature, exceptions ), visitStub );
      } else {
        return null;
      }
    } else {
      return new OutputMethodVisitor( cv.visitMethod( access, name, desc, signature, exceptions ) );
    }
  }

  private void visitStub( MethodVisitor mv ) {
    mv.visitCode();
    mv.visitTypeInsn( Opcodes.NEW, "java/lang/InternalError" );
    mv.visitInsn( Opcodes.DUP );
    mv.visitLdcInsn( "Badly shrinked" );
    mv.visitMethodInsn( Opcodes.INVOKESPECIAL, "java/lang/InternalError", "<init>", "(Ljava/lang/String;)V", currentClass.isInterface() );
    mv.visitInsn( Opcodes.ATHROW );
    mv.visitMaxs( 3, 1 );
  }

  public void visitEnd() {
    cv.visitEnd();
  }

  /**
   * Gets num obsolete methods.
   *
   * @return the num obsolete methods
   */
  public int getNumObsoleteMethods() {
    return numObsoleteMethods;
  }

  /**
   * Gets num obsolete fields.
   *
   * @return the num obsolete fields
   */
  public int getNumObsoleteFields() {
    return numObsoleteFields;
  }


  /**
   * The type Output method visitor.
   */
  class OutputMethodVisitor extends MethodVisitor {

    private MethodVisitor delegate;

    /**
     * Instantiates a new Output method visitor.
     *
     * @param delegate the delegate
     */
    public OutputMethodVisitor( MethodVisitor delegate ) {
      super(Opcodes.ASM7);
      this.delegate = delegate;
    }

    // visit default value in Annotation interface
    public AnnotationVisitor visitAnnotationDefault() {
      return delegate.visitAnnotationDefault();
    }

    public AnnotationVisitor visitAnnotation( String desc, boolean visible ) {
      if ( visible ) {
        if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeVisibleAnnotations ) ) {
          return ignoreAnnotation;
        }
      } else {
        if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeInvisibleAnnotations ) ) {
          return ignoreAnnotation;
        }
      }

      return new OutputAnnotationVisitor( delegate.visitAnnotation( desc, visible ) );
    }

    public AnnotationVisitor visitParameterAnnotation( int parameter, String desc, boolean visible ) {
      if ( visible ) {
        if ( !currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeVisibleParameterAnnotations ) ) {
          return ignoreAnnotation;
        }
      } else {
        if ( !currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeInvisibleParameterAnnotations ) ) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor( delegate.visitParameterAnnotation( parameter, desc, visible ) );
    }

    public AnnotationVisitor visitInsnAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible
    ) {
      if (visible) {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeVisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      } else {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor(delegate.visitInsnAnnotation(typeRef, typePath, descriptor, visible));
    }

    public AnnotationVisitor visitLocalVariableAnnotation(
      final int typeRef, final TypePath typePath, final Label[] start, final Label[] end, final int[] index, final String descriptor, final boolean visible
    ) {
      if (visible) {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeVisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      } else {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor(delegate.visitLocalVariableAnnotation(typeRef, typePath,start, end, index, descriptor, visible));
    }

    public AnnotationVisitor visitTypeAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible
    ) {
      if (visible) {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeVisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      } else {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor(delegate.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible
    ) {
      if (visible) {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeVisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      } else {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor(delegate.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible));
    }

    public void visitAttribute( Attribute attr ) {
      if ( currentClass.getRetainAttribute( attr.type ) ) {
        delegate.visitAttribute( attr );
      }
    }

    public void visitCode() {
      delegate.visitCode();
    }

    // asm 3.1
    public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
      delegate.visitFrame(i, i1, objects, i2, objects1);
    }


    public void visitInsn( int opcode ) {
      delegate.visitInsn( opcode );
    }

    public void visitIntInsn( int opcode, int operand ) {
      delegate.visitIntInsn( opcode, operand );
    }

    public void visitVarInsn( int opcode, int var ) {
      delegate.visitVarInsn( opcode, var );
    }

    public void visitTypeInsn( int opcode, String desc ) {
      delegate.visitTypeInsn( opcode, desc );
    }

    public void visitFieldInsn( int opcode, String owner, String name, String desc ) {
      delegate.visitFieldInsn( opcode, owner, name, desc );
    }

    public void visitMethodInsn( int opcode, String owner, String name, String desc, boolean itf ) {
      delegate.visitMethodInsn( opcode, owner, name, desc, itf );
    }

    public void visitJumpInsn( int opcode, Label label ) {
      delegate.visitJumpInsn( opcode, label );
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
      delegate.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    public void visitLabel( Label label ) {
      delegate.visitLabel( label );
    }

    public void visitLdcInsn( Object cst ) {
      delegate.visitLdcInsn( cst );
    }

    public void visitIincInsn( int var, int increment ) {
      delegate.visitIincInsn( var, increment );
    }

    public void visitTableSwitchInsn( int min, int max, Label dflt, Label... labels ) {
      delegate.visitTableSwitchInsn( min, max, dflt, labels );
    }

    public void visitLookupSwitchInsn( Label dflt, int[] keys, Label[] labels ) {
      delegate.visitLookupSwitchInsn( dflt, keys, labels );
    }

    public void visitMultiANewArrayInsn( String desc, int dims ) {
      delegate.visitMultiANewArrayInsn( desc, dims );
    }

    public void visitTryCatchBlock( Label start, Label end, Label handler, String type ) {
      delegate.visitTryCatchBlock( start, end, handler, type );
    }

    public void visitLocalVariable( String name, String desc, String signature, Label start, Label end, int index ) {
      if ( null != signature ) {
        if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_LocalVariableTypeTable ) ) {
          return;
        }
      } else {
        if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_LocalVariableTable ) ) {
          return;
        }
      }
      delegate.visitLocalVariable( name, desc, signature, start, end, index );
    }

    public void visitLineNumber( int line, Label start ) {
      if ( currentClass.getRetainAttribute( ClassConstants.ATTR_LineNumberTable ) ) {
        delegate.visitLineNumber( line, start );
      }
    }

    public void visitMaxs( int maxStack, int maxLocals ) {
      delegate.visitMaxs( maxStack, maxLocals );
    }

    public void visitEnd() {
      delegate.visitEnd();
    }
  }

  /**
   * The type Output field visitor.
   */
  class OutputFieldVisitor extends FieldVisitor {

    private final FieldVisitor delegate;

    /**
     * Instantiates a new Output field visitor.
     *
     * @param delegate the delegate
     */
    public OutputFieldVisitor(FieldVisitor delegate) {
      super(Opcodes.ASM7);
      this.delegate = delegate;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
       if ( visible ) {
         if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeVisibleAnnotations ) ) {
           return ignoreAnnotation;
         }
       } else {
         if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeInvisibleAnnotations ) ) {
           return ignoreAnnotation;
         }
       }
      return new OutputAnnotationVisitor( delegate.visitAnnotation( desc, visible ) );
    }

    public AnnotationVisitor visitTypeAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible
    ) {
      if (visible) {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeVisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      } else {
        if (!currentClass.getRetainAttribute(ClassConstants.ATTR_RuntimeInvisibleTypeAnnotations)) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor(delegate.visitTypeAnnotation(typeRef, typePath, descriptor, visible));
    }

    public void visitAttribute(Attribute attribute) {
      if ( currentClass.getRetainAttribute( attribute.type ) ) {
        delegate.visitAttribute( attribute );
      }
    }

    public void visitEnd() {
      delegate.visitEnd();
    }
  }

  /**
   * The type Stub output method visitor.
   */
  class StubOutputMethodVisitor extends MethodVisitor {

    private MethodVisitor delegate;
    private final boolean visitStub;

    /**
     * Instantiates a new Stub output method visitor.
     *
     * @param delegate  the delegate
     * @param visitStub the visit stub
     */
    public StubOutputMethodVisitor(MethodVisitor delegate, boolean visitStub) {
      super(Opcodes.ASM7);
      this.delegate = delegate;
      this.visitStub = visitStub;
    }

    // visit default value in Annotation interface
    public AnnotationVisitor visitAnnotationDefault() {
      return delegate.visitAnnotationDefault();
    }

    public AnnotationVisitor visitAnnotation( String desc, boolean visible ) {

      if ( visible ) {
        if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeVisibleAnnotations ) ) {
          return ignoreAnnotation;
        }
      } else {
        if ( ! currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeInvisibleAnnotations ) ) {
          return ignoreAnnotation;
        }
      }

      return new OutputAnnotationVisitor( delegate.visitAnnotation( desc, visible ) );
    }

    public AnnotationVisitor visitParameterAnnotation( int parameter, String desc, boolean visible ) {
      if ( visible ) {
        if ( !currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeVisibleParameterAnnotations ) ) {
          return ignoreAnnotation;
        }
      } else {
        if ( !currentClass.getRetainAttribute( ClassConstants.ATTR_RuntimeInvisibleParameterAnnotations ) ) {
          return ignoreAnnotation;
        }
      }
      return new OutputAnnotationVisitor( delegate.visitParameterAnnotation( parameter, desc, visible ) );
    }

    public void visitAttribute( Attribute attr ) {
      if ( currentClass.getRetainAttribute( attr.type ) ) {
        delegate.visitAttribute( attr );
      }
    }

    public void visitCode() {
    }

    public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
    }

    public void visitInsn( int opcode ) {
    }

    public void visitIntInsn( int opcode, int operand ) {
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
    }

    public void visitVarInsn( int opcode, int var ) {
    }

    public void visitTypeInsn( int opcode, String desc ) {
    }

    public void visitFieldInsn( int opcode, String owner, String name, String desc ) {
    }

    public void visitMethodInsn( int opcode, String owner, String name, String desc, boolean itf ) {
    }

    public void visitJumpInsn( int opcode, Label label ) {
    }

    public void visitLabel( Label label ) {
    }

    public void visitLdcInsn( Object cst ) {
    }

    public void visitIincInsn( int var, int increment ) {
    }

    public void visitTableSwitchInsn( int min, int max, Label dflt, Label... labels ) {
    }

    public void visitLookupSwitchInsn( Label dflt, int[] keys, Label[] labels ) {
    }

    public void visitMultiANewArrayInsn( String desc, int dims ) {
    }

    public void visitTryCatchBlock( Label start, Label end, Label handler, String type ) {
    }

    public void visitLocalVariable( String name, String desc, String signature, Label start, Label end, int index ) {
    }

    public void visitLineNumber( int line, Label start ) {
    }

    public void visitMaxs( int maxStack, int maxLocals ) {
    }

    public void visitEnd() {
      if (visitStub) {
        visitStub(delegate);
      }
      delegate.visitEnd();
    }
  }

  /**
   * can't return null where returntype is AnnotationVisitor..
   */
  static class DoNothingAnnotationVisitor extends AnnotationVisitor {

    /**
     * Instantiates a new Do nothing annotation visitor.
     */
    public DoNothingAnnotationVisitor() {
      super(Opcodes.ASM7);
    }

    public void visit( String name, Object value ) {
    }

    public void visitEnum( String name, String desc, String value ) {
    }

    public AnnotationVisitor visitAnnotation( String name, String desc ) {
      return this;
    }

    public AnnotationVisitor visitArray( String name ) {
      return this;
    }

    public void visitEnd() {
    }
  }


  /**
   * currently just delegates all methods to <code>delegate</code>.
   */
  class OutputAnnotationVisitor extends AnnotationVisitor {


    /**
     * The Delegate.
     */
    AnnotationVisitor delegate;

    /**
     * Instantiates a new Output annotation visitor.
     *
     * @param delegate the delegate
     */
    public OutputAnnotationVisitor( AnnotationVisitor delegate ) {
      super(Opcodes.ASM7);
      this.delegate = delegate;
    }

    public void visit( String name, Object value ) {
      delegate.visit( name, value );
    }

    public void visitEnum( String name, String desc, String value ) {
      delegate.visitEnum( name, desc, value );
    }

    public AnnotationVisitor visitAnnotation( String name, String desc ) {
      return new OutputAnnotationVisitor( delegate.visitAnnotation( name, desc ) );
    }

    public AnnotationVisitor visitArray( String name ) {
      return new OutputAnnotationVisitor( delegate.visitArray( name ) );
    }

    public void visitEnd() {
      delegate.visitEnd();
    }
  }

}
