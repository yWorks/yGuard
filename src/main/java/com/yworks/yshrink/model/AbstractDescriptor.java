package com.yworks.yshrink.model;

import org.objectweb.asm.Opcodes;
import com.yworks.graph.Node;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.yworks.logging.Logger;

/**
 * The type Abstract descriptor.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class AbstractDescriptor {

  private boolean isEntryPoint;
  private boolean isReachable;
  private List<AnnotationUsage> annotations = new ArrayList<AnnotationUsage>();

    /**
     * The Node.
     */
    protected Node node;
    /**
     * The Access.
     */
    protected final int access;

    /**
     * The Source jar.
     */
    protected File sourceJar;
  private static final Pattern CLASS_PATTERN = Pattern.compile("L(.*);");

    /**
     * Instantiates a new Abstract descriptor.
     *
     *
		 * @param access    the access
     *
		 * @param sourceJar the source jar
     */
    protected AbstractDescriptor( int access, File sourceJar ) {
    this.access = access;
    this.sourceJar = sourceJar;
  }

    /**
     * Add annotation annotation usage.
     *
     *
		 * @param annotationName the annotation name
     * 
		 * @return the annotation usage
     */
    public AnnotationUsage addAnnotation(String annotationName) {
    Matcher matcher = CLASS_PATTERN.matcher(annotationName);
    if (matcher.matches()){
      AnnotationUsage usage = new AnnotationUsage(matcher.group(1));
      annotations.add(usage);
      return usage;
    } else {
      Logger.warn("Unexpected annotation name: "+annotationName);
      return new AnnotationUsage(annotationName);
    }
  }

    /**
     * Gets annotations.
     *
     * 
		 * @return the annotations
     */
    public List<AnnotationUsage> getAnnotations() {
    return annotations;
  }

    /**
     * Gets node.
     *
     * 
		 * @return the node
     */
    public Node getNode() {
    return node;
  }

    /**
     * Sets node.
     *
     *
		 * @param node the node
     */
    public void setNode( final Node node ) {
    this.node = node;
  }

    /**
     * Is entry point boolean.
     *
     * 
		 * @return the boolean
     */
    public boolean isEntryPoint() {
    return isEntryPoint;
  }

    /**
     * Sets entry point.
     *
     *
		 * @param entryPoint the entry point
     */
    public void setEntryPoint( final boolean entryPoint ) {
    isEntryPoint = entryPoint;
  }

    /**
     * Sets reachable.
     *
     *
		 * @param reachable the reachable
     */
    public void setReachable( final boolean reachable ) {
    isReachable = reachable;
  }

    /**
     * Gets access.
     *
     * 
		 * @return the access
     */
    public int getAccess() {
    return access;
  }

    /**
     * Is synthetic boolean.
     *
     * 
		 * @return the boolean
     */
    public boolean isSynthetic() {
    return ( access & Opcodes.ACC_SYNTHETIC ) == Opcodes.ACC_SYNTHETIC;
  }

    /**
     * Is abstract boolean.
     *
     * 
		 * @return the boolean
     */
    public boolean isAbstract() {
    return ( Opcodes.ACC_ABSTRACT & access) == Opcodes.ACC_ABSTRACT;
  }

    /**
     * Gets source jar.
     *
     * 
		 * @return the source jar
     */
    public File getSourceJar() {
    return this.sourceJar;
  }
}
