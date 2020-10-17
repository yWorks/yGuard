package com.yworks.yshrink.model;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;

/**
 * The type Model visitor.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class ModelVisitor extends ClassVisitor {
    /**
     * The Opcodes asm.
     */
    static final int OPCODES_ASM = Opcodes.ASM7;

  private Model model;

  private ClassDescriptor currentClass;
  private final File sourceJar;

    /**
     * Instantiates a new Model visitor.
     *
     * @param model     the model
     * @param sourceJar the source jar
     */
    public ModelVisitor( final Model model, final File sourceJar ) {
    super(OPCODES_ASM);
    this.model = model;
    this.sourceJar = sourceJar;
  }

  //
  //  ClassVisitor
  //

  public void visit( final int version, final int access, final String name, final String signature,
                     final String superName, final String[] interfaces ) {

    if ( ! model.isClassModeled( name ) ) {
      currentClass = model.newClassDescriptor( name, superName, interfaces, access, sourceJar );
    } else {
      currentClass = model.getClassDescriptor( name );
      currentClass.setInterfaces( interfaces );
      currentClass.setSuperName( superName );
    }
  }

  public void visitInnerClass( final String name, final String outerName, final String innerName, final int access ) {

  }

  public void visitNestMember(java.lang.String nestMember) {
    currentClass.setHasNestMembers(true);
  }

  public void visitOuterClass( final String owner, final String name, final String desc ) {
    if ( name != null ) { // class declared in a method
      currentClass.setEnclosingMethod( name, desc );
      currentClass.setEnclosingClass(owner);
    } else {
      currentClass.setEnclosingClass( owner );
    }
  }

  public FieldVisitor visitField( final int access, final String name, final String desc, final String signature,
                                  final Object value ) {
    model.newFieldDescriptor( currentClass, desc, name, access, sourceJar );
    return null;
  }

  public MethodVisitor visitMethod( final int access, final String name, final String desc, final String signature,
                                    final String[] exceptions ) {
    MethodDescriptor currentMethod = model.newMethodDescriptor(currentClass, access, name, desc, exceptions, sourceJar);
    return new ModelMethodVisitor(currentMethod);
  }

  public void visitSource( final String source, final String debug ) {
  }

  public AnnotationVisitor visitAnnotation( final String desc, final boolean visible ) {
    return new ModelAnnotationVisitor(currentClass, currentClass.addAnnotation(desc));
  }

  public void visitAttribute( final Attribute attr ) {
  }

  public void visitEnd() {
  }

    /**
     * The type Model method visitor.
     */
    class ModelMethodVisitor extends MethodVisitor {

    private MethodDescriptor currentMethod;

        /**
         * Instantiates a new Model method visitor.
         *
         * @param currentMethod the current method
         */
        public ModelMethodVisitor( MethodDescriptor currentMethod ) {
      // increasing ASM opcodes level from 5 to 7 forces ASM to accept
      // CONSTANT_Dynamic_info byte code instructions
      //
      // strictly speaking this would be not necessary because yGuard does
      // explicitly not support obfuscating byte code that uses
      // CONSTANT_Dynamic_info instructions, however doing so will prevent ASM
      // from throwing an UnsupportedOperationException when parsing
      // CONSTANT_Dynamic_info instructions which in turn allows yGuard to abort
      // obfuscation with its own error message for those instructions
      super(OPCODES_ASM);
      this.currentMethod = currentMethod;
    }

    public void visitMethodInsn( final int opcode, final String owner, final String name, final String desc, final boolean itf ) {
      currentMethod.addInvocation( opcode, owner, name, desc );
    }

    public void visitTypeInsn( final int opcode, final String desc ) {
      currentMethod.addTypeInstruction( opcode, desc );
    }

    public void visitMultiANewArrayInsn( final String desc, final int dims ) {
      currentMethod.addTypeInstruction( Opcodes.MULTIANEWARRAY, desc );
    }

    public void visitFieldInsn( final int opcode, final String owner, final String name, final String desc ) {
      currentMethod.addFieldRef( owner, name );
    }

    public AnnotationVisitor visitAnnotationDefault() {
      return new ModelAnnotationVisitor(currentMethod, new AnnotationUsage("java.lang.AnnotationDefaultAttribute"));
    }

    public AnnotationVisitor visitAnnotation( final String desc, final boolean visible ) {
      return new ModelAnnotationVisitor(currentMethod, currentMethod.addAnnotation(desc));
    }

    public AnnotationVisitor visitParameterAnnotation( final int parameter, final String desc, final boolean visible ) {
      return new ModelAnnotationVisitor(currentMethod, currentMethod.addAnnotation(desc));
    }

    public void visitAttribute( final Attribute attr ) {
      
    }

    public void visitCode() {
    }

    public void visitFrame(int i, int i1, Object[] objects, int i2, Object[] objects1) {
    }

    public void visitInsn( final int opcode ) {
    }

    public void visitIntInsn( final int opcode, final int operand ) {
    }

    public void visitInvokeDynamicInsn( String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
      if (bootstrapMethodHandle.getOwner().equals("java/lang/invoke/LambdaMetafactory")) {
        String className = descriptor.substring(descriptor.indexOf(")L") + 2, descriptor.length() - 1);
        // find out the method descriptor of the method
        // We expect bootstrapMethodArguments[0] to be method descriptor
        // and bootstrapMethodArguments[1] to be the methods class handle
        Type methodDescriptor = (Type) bootstrapMethodArguments[0];
        Handle callerHandle = (Handle) bootstrapMethodArguments[1];
        currentMethod.addInvocation(Opcodes.INVOKEDYNAMIC, className, name, methodDescriptor.getDescriptor());
        currentMethod.addInvocation(Opcodes.INVOKEDYNAMIC, callerHandle.getOwner(), callerHandle.getName(), callerHandle.getDesc() );
      }
    }

    public void visitVarInsn( final int opcode, final int var ) {
    }

    public void visitJumpInsn( final int opcode, final Label label ) {
    }

    public void visitLabel( final Label label ) {
    }

    public void visitLdcInsn( final Object cst ) {
      if ( cst instanceof Type ) { // .class in class versions >= 0.49
        final Type type = (Type) cst;
        currentMethod.addTypeInstruction( Opcodes.LDC, type.getDescriptor() );
      }
    }

    public void visitIincInsn( final int var, final int increment ) {
    }

    public void visitTableSwitchInsn( final int min, final int max, final Label dflt, final Label... labels ) {
    }

    public void visitLookupSwitchInsn( final Label dflt, final int[] keys, final Label[] labels ) {
    }

    public void visitTryCatchBlock( final Label start, final Label end, final Label handler, final String type ) {
    }

    public void visitLocalVariable( final String name, final String desc, final String signature, final Label start,
                                    final Label end, final int index ) {
    }

    public void visitLineNumber( final int line, final Label start ) {
    }

    public void visitMaxs( final int maxStack, final int maxLocals ) {
    }

    public void visitEnd() {
    }
  }

  //
  // AnnotationVisitor
  //

    /**
     * The type Model annotation visitor.
     */
    class ModelAnnotationVisitor extends AnnotationVisitor {
    private final AbstractDescriptor currentItem;
    private final AnnotationUsage annotationUsage;

        /**
         * Instantiates a new Model annotation visitor.
         *
         * @param currentItem     the current item
         * @param annotationUsage the annotation usage
         */
        public ModelAnnotationVisitor(
        AbstractDescriptor currentItem, AnnotationUsage annotationUsage) {
      super(OPCODES_ASM);
      this.currentItem = currentItem;
      this.annotationUsage = annotationUsage;
    }


    public void visit( String name, Object value ) {
      annotationUsage.addFieldUsage(name);
    }

    public void visitEnum( String name, String desc, String value ) {
      annotationUsage.addFieldUsage(name);
    }

    public AnnotationVisitor visitAnnotation( String name, String desc ) {
      annotationUsage.addFieldUsage(name);
      return new ModelAnnotationVisitor(currentItem, currentItem.addAnnotation(desc));
    }

    public AnnotationVisitor visitArray( String name ) {
      annotationUsage.addFieldUsage(name);
      return new ModelAnnotationVisitor(currentItem, new AnnotationUsage("array"));
    }

    public void visitEnd() {
    }
  }
}
