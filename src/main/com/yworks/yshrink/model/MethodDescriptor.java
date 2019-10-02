package com.yworks.yshrink.model;

import com.yworks.yshrink.util.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class MethodDescriptor extends AbstractDescriptor {

  private String name;
  private String desc;
  private List<Invocation> invocations; // call: [type,methodName,methodDesc]
  private List<String[]> fieldRefs; // fieldRef: [owner,name]
  private List<AbstractMap.SimpleEntry<Object, Object>> typeInstructions;
  private String[] exceptions;
  private List<String> localVars;

  protected MethodDescriptor( final String name, final int access, final String desc, final String[] exceptions, File sourceJar ) {

    super( access, sourceJar );
    this.name = name;
    this.desc = desc;
    invocations = new ArrayList<>();
    fieldRefs = new ArrayList<>();
    typeInstructions = new ArrayList<>();
    localVars = new ArrayList<>();
    this.exceptions = exceptions;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public Type[] getArgumentTypes() {
    return Type.getArgumentTypes( desc );
  }

  public String getArgumentsString() {
    StringBuilder buf = new StringBuilder();
    Type[] argumentTypes = getArgumentTypes();
    for ( Type type : argumentTypes ) {
      buf.append( type.getDescriptor() );
    }
    return buf.toString();
  }

  public Type getReturnType() {
    return Type.getReturnType( desc );
  }

  public List<Invocation> getInvocations() {
    return invocations;
  }

  public void addInvocation( final int opcode, final String type, final String name, final String desc ) {

    invocations.add( InvocationFactory.getInstance().getInvocation( opcode, type, name, desc ) );
    //invocations.add( new Invocation( opcode, type, name, desc ) );
  }

  public List<String[]> getFieldRefs() {
    return fieldRefs;
  }

  public void addFieldRef( final String type, final String name ) {
    fieldRefs.add( new String[]{ type, name } );
  }

  public void addTypeInstruction( final int opcode, final String desc ) {
    typeInstructions.add( new AbstractMap.SimpleEntry<Object, Object>( opcode, desc ));
  }

  public void addLocalVar( final String desc ) {
    localVars.add( desc );
  }

  public List<AbstractMap.SimpleEntry<Object, Object>> getTypeInstructions() {
    return typeInstructions;
  }

  public String[] getExceptions() {
    return exceptions;
  }

  public boolean isStatic() {
    return ( access & Opcodes.ACC_STATIC ) == Opcodes.ACC_STATIC;
  }

  public boolean isPrivate() {
    return ( access & Opcodes.ACC_PRIVATE ) == Opcodes.ACC_PRIVATE;
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

    return overrides( md.getName(), md.getReturnType(), md.getArgumentTypes() );
  }

  public boolean overrides( final Method m ) {
    return overrides( m.getName(), Type.getReturnType( m ), Type.getArgumentTypes( m ) );
  }

  private boolean overrides( String mName, Type mReturnType, Type[] mArgumentTypes ) {

    if ( ! mName.equals( getName() ) ) return false;

    if ( ! getReturnType().equals( mReturnType ) ) return false;

    final Type[] argumentTypes = getArgumentTypes();

    final Type[] argumentTypesMd = mArgumentTypes;

    if ( argumentTypes.length != argumentTypesMd.length ) {
      return false;
    } else {
      for ( int i = 0; i < argumentTypes.length; i++ ) {
        if ( !argumentTypes[ i ].equals( argumentTypesMd[ i ] ) ) {
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

  public boolean isConstructor() {
    return getName().equals( Model.CONSTRUCTOR_NAME );
  }

  public String getSignature() {

    final StringBuilder buf = new StringBuilder();

    buf.append( Util.toJavaType( getReturnType().getDescriptor() ) ).append( " " ).append(
        getName() ).append( "(" );
    Type[] argumentTypes = getArgumentTypes();
    for ( int i = 0; i < argumentTypes.length-1; i++ ) {
      Type type = argumentTypes[ i ];
      buf.append( Util.toJavaType( type.getDescriptor() )).append( "," );
    }
    if ( argumentTypes.length > 0 ) {
      buf.append( Util.toJavaType( argumentTypes[ argumentTypes.length - 1 ].getDescriptor() ) );
    }
    buf.append( ")" );

    return buf.toString();

  }

}
