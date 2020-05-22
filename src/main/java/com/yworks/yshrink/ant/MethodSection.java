package com.yworks.yshrink.ant;

import com.yworks.yguard.common.ant.TypePatternSet;
import com.yworks.yguard.common.ant.PatternMatchedSection;
import com.yworks.yshrink.util.Util;
import org.objectweb.asm.Type;

import java.util.EnumSet;

/**
 * Used by ant to handle the <code>method</code> element.
 */
public final class MethodSection extends PatternMatchedSection {

  private String signature;
  private String name;
  private String className;
  private String returnType;
  private String args;
  private String throwsClause;


  {
    types = EnumSet.of(
        TypePatternSet.Type.NAME,
        TypePatternSet.Type.CLASS
    );
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature( String signature ) {
    this.signature = signature;
    String[] methodArr = Util.toNativeMethod( signature );
    String methodName = methodArr[ 0 ];
    String methodDesc = methodArr[ 1 ];
    setName( methodName );
    setReturnType( Util.toJavaType( Type.getReturnType( methodDesc ).getDescriptor() ) );

    setArgs( Util.getArgumentString( Type.getArgumentTypes( methodDesc ) ) );
  }

  public String getArgs() {
    return args;
  }

  public void setArgs( String args ) {
    this.args = args;
  }

  public void setName( String name ) {

    // in yGuard, the name-attribute is the signature.
    if ( name.trim().indexOf( ' ' ) != -1 ) {
      setSignature( name );
    } else {
      this.name = name;
    }
  }

  public void setClass( String name ) {
    this.className = Util.toInternalClass( name );
  }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType( String returnType ) {
    this.returnType = Util.toInternalClass( returnType );
  }

  public String getThrows() {
    return throwsClause;
  }

  public void setThrows( String throwsClause ) {
    this.throwsClause = throwsClause;
  }

  @Override
  public TypePatternSet createPatternSet() {
    System.out.println( "MethodSection.createPatternSet" );
    TypePatternSet typePatternSet = new TypePatternSet();
    typePatternSet.setType( "class" );
    addPatternSet( typePatternSet, typePatternSet.getType() );
    return typePatternSet;
  }
}
