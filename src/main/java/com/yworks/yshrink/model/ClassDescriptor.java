package com.yworks.yshrink.model;

import org.objectweb.asm.Opcodes;
import com.yworks.graph.Node;

import java.io.File;
import java.util.*;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class ClassDescriptor extends AbstractDescriptor {

  private String name;
  private String superName;
  private String[] interfaces;
  private String enclosingClass;
  private AbstractMap.SimpleEntry<Object, Object> enclosingMethod;
  private boolean hasNestMembers = false;

  private Map<AbstractMap.SimpleEntry<Object, Object>, MethodDescriptor> methods;
  private Map<String, FieldDescriptor> fields;

  private Set<String> allInterfaces;
  private Set<String> allAncestors;

  private Node newNode;

  private boolean hasExternalAncestors = false;

  private Set<String> attributesToKeep = new HashSet<String>();

  protected ClassDescriptor( final String name, final int access, Node newNode, File sourceJar ) {

    super( access, sourceJar );

    this.name = name;
    this.newNode = newNode;
    methods = new HashMap<>();
    fields = new HashMap<>();
  }

  protected ClassDescriptor( final String name, final String superName, final String[] interfaces, final int access, Node newNode, File sourceJar ) {

    this( name, access, newNode, sourceJar );
    this.superName = superName;
    this.interfaces = interfaces;
  }

  public void setEnclosingClass( final String enclosingClass ) {
    this.enclosingClass = enclosingClass;
  }

  public void setEnclosingMethod( final String methodName, final String methodDesc ) {
    this.enclosingMethod = new AbstractMap.SimpleEntry<Object, Object>( methodName, methodDesc );
  }

  public String getEnclosingClass() {
    return enclosingClass;
  }

  public AbstractMap.SimpleEntry<Object, Object> getEnclosingMethod() {
    return enclosingMethod;
  }

  public void addMethod( final MethodDescriptor method ) {
    methods.put( new AbstractMap.SimpleEntry<Object, Object>( method.getName(), method.getDesc() ), method );
  }

  public void addField( final FieldDescriptor field ) {
    fields.put( field.getName(), field );
  }

  public void setHasExternalAncestors( final boolean hasExternalAncestors ) {
    this.hasExternalAncestors = hasExternalAncestors;
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    final int i = name.lastIndexOf( '/' );
    if ( i != -1 ) {
      return name.substring( i + 1, name.length() );
    } else {
      return name;
    }
  }

  public String getSuperName() {
    return superName;
  }

  public void setSuperName( final String superName ) {
    this.superName = superName;
  }

  public void setInterfaces( final String[] interfaces ) {
    this.interfaces = interfaces;
  }

  public String[] getInterfaces() {
    return interfaces;
  }

  public MethodDescriptor getMethod( final String name, final String desc ) {
    return methods.get( new AbstractMap.SimpleEntry<Object, Object>( name, desc ));
  }

  public MethodDescriptor getMethod( final AbstractMap.SimpleEntry<Object, Object> method ) {
    return methods.get( method );
  }

  public FieldDescriptor getField( final String name ) {
    return fields.get( name );
  }

  public Collection<MethodDescriptor> getMethods() {
    return methods.values();
  }

  public boolean isInterface() {
    return ( super.access & Opcodes.ACC_INTERFACE ) != 0;
  }

  public boolean isEnum() {
    return ( super.access & Opcodes.ACC_ENUM ) != 0;
  }

  public boolean isAnnotation() {
    return ( super.access & Opcodes.ACC_ANNOTATION ) != 0;
  }

  public boolean isInnerClass() {
    return enclosingClass != null;
  }

  public boolean implementsMethod( final String methodName, final String methodDesc ) {
    return methods.containsKey( new AbstractMap.SimpleEntry<Object, Object>( methodName, methodDesc ));
  }

  public boolean declaresField( final String fieldName ) {
    return fields.containsKey( fieldName );
  }

  public Collection<FieldDescriptor> getFields() {
    return fields.values();
  }

  public Set<String> getAllImplementedInterfaces( Model model ) {
    if ( null != allInterfaces ) {
      return allInterfaces;
    } else {
      allInterfaces = new HashSet<String>( 3 );
      model.getAllImplementedInterfaces( getName(), allInterfaces );
    }
    return allInterfaces;
  }

  public Set<String> getAllAncestorClasses( Model model ) {
    if ( null != allAncestors ) {
      return allAncestors;
    } else {
      allAncestors = new HashSet<String>( 3 );
      model.getAllAncestorClasses( getName(), allAncestors );
    }
    return allAncestors;
  }

  public String toString() {
    return "ClassDescriptor{" +
        "name='" + name + '\'' +
        ", enclosingClass='" + enclosingClass + '\'' +
        ", enclosingMethod=" + enclosingMethod +
        '}';
  }

  public Node getNewNode() {
    return this.newNode;
  }

  public void setRetainAttribute( String attr ) {
    attributesToKeep.add( attr );
  }

  public boolean getRetainAttribute( String attr ) {
    return attributesToKeep.contains( attr );
  }

  public boolean getHasNestMembers() {
    return hasNestMembers;
  }

  public void setHasNestMembers(boolean nestMembers) {
    hasNestMembers = nestMembers;
  }

}
