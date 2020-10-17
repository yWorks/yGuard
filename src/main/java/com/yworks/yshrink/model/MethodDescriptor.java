package com.yworks.yshrink.model;

import com.yworks.yshrink.util.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Method descriptor.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class MethodDescriptor extends AbstractDescriptor {

  private final String name;
  private final String desc;
  private final List<Invocation> invocations; // call: [type,methodName,methodDesc]
  private final List<String[]> fieldRefs; // fieldRef: [owner,name]
  private final List<AbstractMap.SimpleEntry<Object, Object>> typeInstructions;
  private final String[] exceptions;
  private final List<String> localVars;

  /**
   * Instantiates a new Method descriptor.
   *
   * @param name       the name
   * @param access     the access
   * @param desc       the desc
   * @param exceptions the exceptions
   * @param sourceJar  the source jar
   */
  protected MethodDescriptor( final String name, final int access, final String desc, final String[] exceptions, File sourceJar ) {

    super(access, sourceJar);
    this.name = name;
    this.desc = desc;
    invocations = new ArrayList<>();
    fieldRefs = new ArrayList<>();
    typeInstructions = new ArrayList<>();
    localVars = new ArrayList<>();
    this.exceptions = exceptions;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets desc.
   *
   * @return the desc
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Get argument types type [ ].
   *
   * @return the type [ ]
   */
  public Type[] getArgumentTypes() {
    return Type.getArgumentTypes(desc);
  }

  /**
   * Gets arguments string.
   *
   * @return the arguments string
   */
  public String getArgumentsString() {
    StringBuilder buf = new StringBuilder();
    Type[] argumentTypes = getArgumentTypes();
    for (Type type : argumentTypes) {
      buf.append(type.getDescriptor());
    }
    return buf.toString();
  }

  /**
   * Gets return type.
   *
   * @return the return type
   */
  public Type getReturnType() {
    return Type.getReturnType(desc);
  }

  /**
   * Gets invocations.
   *
   * @return the invocations
   */
  public List<Invocation> getInvocations() {
    return invocations;
  }

  /**
   * Add invocation.
   *
   * @param opcode the opcode
   * @param type   the type
   * @param name   the name
   * @param desc   the desc
   */
  public void addInvocation( final int opcode, final String type, final String name, final String desc ) {

    invocations.add(InvocationFactory.getInstance().getInvocation(opcode, type, name, desc));
    //invocations.add( new Invocation( opcode, type, name, desc ) );
  }

  /**
   * Gets field refs.
   *
   * @return the field refs
   */
  public List<String[]> getFieldRefs() {
    return fieldRefs;
  }

  /**
   * Add field ref.
   *
   * @param type the type
   * @param name the name
   */
  public void addFieldRef( final String type, final String name ) {
    fieldRefs.add(new String[]{type, name});
  }

  /**
   * Add type instruction.
   *
   * @param opcode the opcode
   * @param desc   the desc
   */
  public void addTypeInstruction( final int opcode, final String desc ) {
    typeInstructions.add(new AbstractMap.SimpleEntry<Object, Object>(opcode, desc));
  }

  /**
   * Add local var.
   *
   * @param desc the desc
   */
  public void addLocalVar( final String desc ) {
    localVars.add(desc);
  }

  /**
   * Gets type instructions.
   *
   * @return the type instructions
   */
  public List<AbstractMap.SimpleEntry<Object, Object>> getTypeInstructions() {
    return typeInstructions;
  }

  /**
   * Get exceptions string [ ].
   *
   * @return the string [ ]
   */
  public String[] getExceptions() {
    return exceptions;
  }

  /**
   * Has flag boolean.
   *
   * @param code the code
   * @return the boolean
   */
  public boolean hasFlag( int code ) {
    return (access & code) == code;
  }

  /**
   * Is static boolean.
   *
   * @return the boolean
   */
// TODO: Refactor usages of isStatic and isPrivate with hasFlag
  public boolean isStatic() {
    return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
  }

  /**
   * Is private boolean.
   *
   * @return the boolean
   */
  public boolean isPrivate() {
    return (access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
  }

  public int getAccess() {
    return access;
  }

  /**
   * check wether method md could override this method. <ul> <li>same name</li> <li><code>md</code> has same return type
   * or return type of <code>md</code> is subclass of the return type of this method. </li> <li>same number and type of
   * arguments</li> <li>md throws only the same or subclasses of Exceptions listed in the throws clause of this
   * method</li> <li>access: same or more</li> </ul>
   *
   * @param md the MethodDescriptor
   * @return true iff all constraints given above are true
   */
  public boolean overrides( final MethodDescriptor md ) {

    return overrides(md.getName(), md.getReturnType(), md.getArgumentTypes());
  }

  /**
   * Overrides boolean.
   *
   * @param m the m
   * @return the boolean
   */
  public boolean overrides( final Method m ) {
    return overrides(m.getName(), Type.getReturnType(m), Type.getArgumentTypes(m));
  }

  private boolean overrides( String mName, Type mReturnType, Type[] mArgumentTypes ) {

    if (!mName.equals(getName())) {
      return false;
    }

    if (!getReturnType().equals(mReturnType)) {
      return false;
    }

    final Type[] argumentTypes = getArgumentTypes();

    final Type[] argumentTypesMd = mArgumentTypes;

    if (argumentTypes.length != argumentTypesMd.length) {
      return false;
    } else {
      for (int i = 0; i < argumentTypes.length; i++) {
        if (!argumentTypes[i].equals(argumentTypesMd[i])) {
          return false;
        }
      }
    }

    return true;
  }

  public String toString() {
    return "MethodDescriptor{" +
           "name='" + name + '\'' +
           ", desc='" + desc + '\'' +
           '}';
  }

  /**
   * Is constructor boolean.
   *
   * @return the boolean
   */
  public boolean isConstructor() {
    return getName().equals(Model.CONSTRUCTOR_NAME);
  }

  /**
   * Gets signature.
   *
   * @return the signature
   */
  public String getSignature() {

    final StringBuilder buf = new StringBuilder();

    buf.append(Util.toJavaType(getReturnType().getDescriptor())).append(" ").append(
            getName()).append("(");
    Type[] argumentTypes = getArgumentTypes();
    for (int i = 0; i < argumentTypes.length - 1; i++) {
      Type type = argumentTypes[i];
      buf.append(Util.toJavaType(type.getDescriptor())).append(",");
    }
    if (argumentTypes.length > 0) {
      buf.append(Util.toJavaType(argumentTypes[argumentTypes.length - 1].getDescriptor()));
    }
    buf.append(")");

    return buf.toString();

  }

}
