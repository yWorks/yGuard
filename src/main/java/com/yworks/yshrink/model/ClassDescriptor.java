package com.yworks.yshrink.model;

import com.yworks.graph.Node;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Class descriptor.
 *
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

    /**
     * Instantiates a new Class descriptor.
     *
     * @param name      the name
     * @param access    the access
     * @param newNode   the new node
     * @param sourceJar the source jar
     */
    protected ClassDescriptor( final String name, final int access, Node newNode, File sourceJar ) {

    super( access, sourceJar );

    this.name = name;
    this.newNode = newNode;
    methods = new HashMap<>();
    fields = new HashMap<>();
  }

    /**
     * Instantiates a new Class descriptor.
     *
     * @param name       the name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param access     the access
     * @param newNode    the new node
     * @param sourceJar  the source jar
     */
    protected ClassDescriptor( final String name, final String superName, final String[] interfaces, final int access, Node newNode, File sourceJar ) {

    this( name, access, newNode, sourceJar );
    this.superName = superName;
    this.interfaces = interfaces;
  }

    /**
     * Sets enclosing class.
     *
     * @param enclosingClass the enclosing class
     */
    public void setEnclosingClass( final String enclosingClass ) {
    this.enclosingClass = enclosingClass;
  }

    /**
     * Sets enclosing method.
     *
     * @param methodName the method name
     * @param methodDesc the method desc
     */
    public void setEnclosingMethod( final String methodName, final String methodDesc ) {
    this.enclosingMethod = new AbstractMap.SimpleEntry<Object, Object>( methodName, methodDesc );
  }

    /**
     * Gets enclosing class.
     *
     * @return the enclosing class
     */
    public String getEnclosingClass() {
    return enclosingClass;
  }

    /**
     * Gets enclosing method.
     *
     * @return the enclosing method
     */
    public AbstractMap.SimpleEntry<Object, Object> getEnclosingMethod() {
    return enclosingMethod;
  }

    /**
     * Add method.
     *
     * @param method the method
     */
    public void addMethod( final MethodDescriptor method ) {
    methods.put( new AbstractMap.SimpleEntry<Object, Object>( method.getName(), method.getDesc() ), method );
  }

    /**
     * Add field.
     *
     * @param field the field
     */
    public void addField( final FieldDescriptor field ) {
    fields.put( field.getName(), field );
  }

    /**
     * Sets has external ancestors.
     *
     * @param hasExternalAncestors the has external ancestors
     */
    public void setHasExternalAncestors( final boolean hasExternalAncestors ) {
    this.hasExternalAncestors = hasExternalAncestors;
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
     * Gets short name.
     *
     * @return the short name
     */
    public String getShortName() {
    final int i = name.lastIndexOf( '/' );
    if ( i != -1 ) {
      return name.substring( i + 1, name.length() );
    } else {
      return name;
    }
  }

    /**
     * Gets super name.
     *
     * @return the super name
     */
    public String getSuperName() {
    return superName;
  }

    /**
     * Sets super name.
     *
     * @param superName the super name
     */
    public void setSuperName( final String superName ) {
    this.superName = superName;
  }

    /**
     * Sets interfaces.
     *
     * @param interfaces the interfaces
     */
    public void setInterfaces( final String[] interfaces ) {
    this.interfaces = interfaces;
  }

    /**
     * Get interfaces string [ ].
     *
     * @return the string [ ]
     */
    public String[] getInterfaces() {
    return interfaces;
  }

    /**
     * Gets method.
     *
     * @param name the name
     * @param desc the desc
     * @return the method
     */
    public MethodDescriptor getMethod( final String name, final String desc ) {
    return methods.get( new AbstractMap.SimpleEntry<Object, Object>( name, desc ));
  }

    /**
     * Gets method.
     *
     * @param method the method
     * @return the method
     */
    public MethodDescriptor getMethod( final AbstractMap.SimpleEntry<Object, Object> method ) {
    return methods.get( method );
  }

    /**
     * Gets field.
     *
     * @param name the name
     * @return the field
     */
    public FieldDescriptor getField( final String name ) {
    return fields.get( name );
  }

    /**
     * Gets methods.
     *
     * @return the methods
     */
    public Collection<MethodDescriptor> getMethods() {
    return methods.values();
  }

    /**
     * Is interface boolean.
     *
     * @return the boolean
     */
    public boolean isInterface() {
    return ( super.access & Opcodes.ACC_INTERFACE ) != 0;
  }

    /**
     * Is enum boolean.
     *
     * @return the boolean
     */
    public boolean isEnum() {
    return ( super.access & Opcodes.ACC_ENUM ) != 0;
  }

    /**
     * Is annotation boolean.
     *
     * @return the boolean
     */
    public boolean isAnnotation() {
    return ( super.access & Opcodes.ACC_ANNOTATION ) != 0;
  }

    /**
     * Is inner class boolean.
     *
     * @return the boolean
     */
    public boolean isInnerClass() {
    return enclosingClass != null;
  }

    /**
     * Implements method boolean.
     *
     * @param methodName the method name
     * @param methodDesc the method desc
     * @return the boolean
     */
    public boolean implementsMethod( final String methodName, final String methodDesc ) {
    return methods.containsKey( new AbstractMap.SimpleEntry<Object, Object>( methodName, methodDesc ));
  }

    /**
     * Declares field boolean.
     *
     * @param fieldName the field name
     * @return the boolean
     */
    public boolean declaresField( final String fieldName ) {
    return fields.containsKey( fieldName );
  }

    /**
     * Gets fields.
     *
     * @return the fields
     */
    public Collection<FieldDescriptor> getFields() {
    return fields.values();
  }

    /**
     * Gets all implemented interfaces.
     *
     * @param model the model
     * @return the all implemented interfaces
     */
    public Set<String> getAllImplementedInterfaces( Model model ) {
    if ( null != allInterfaces ) {
      return allInterfaces;
    } else {
      allInterfaces = new HashSet<String>( 3 );
      model.getAllImplementedInterfaces( getName(), allInterfaces );
    }
    return allInterfaces;
  }

    /**
     * Gets all ancestor classes.
     *
     * @param model the model
     * @return the all ancestor classes
     */
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

    /**
     * Gets new node.
     *
     * @return the new node
     */
    public Node getNewNode() {
    return this.newNode;
  }

    /**
     * Sets retain attribute.
     *
     * @param attr the attr
     */
    public void setRetainAttribute( String attr ) {
    attributesToKeep.add( attr );
  }

    /**
     * Gets retain attribute.
     *
     * @param attr the attr
     * @return the retain attribute
     */
    public boolean getRetainAttribute( String attr ) {
    return attributesToKeep.contains( attr );
  }

    /**
     * Gets has nest members.
     *
     * @return the has nest members
     */
    public boolean getHasNestMembers() {
    return hasNestMembers;
  }

    /**
     * Sets has nest members.
     *
     * @param nestMembers the nest members
     */
    public void setHasNestMembers(boolean nestMembers) {
    hasNestMembers = nestMembers;
  }

}
