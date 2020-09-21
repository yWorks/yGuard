package com.yworks.yshrink.model;

import org.objectweb.asm.Opcodes;
import com.yworks.util.graph.Node;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.yworks.logging.Logger;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class AbstractDescriptor {

  private boolean isEntryPoint;
  private boolean isReachable;
  private List<AnnotationUsage> annotations = new ArrayList<AnnotationUsage>();

  protected Node node;
  protected final int access;

  protected File sourceJar;
  private static final Pattern CLASS_PATTERN = Pattern.compile("L(.*);");

  protected AbstractDescriptor( int access, File sourceJar ) {
    this.access = access;
    this.sourceJar = sourceJar;
  }

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

  public List<AnnotationUsage> getAnnotations() {
    return annotations;
  }

  public Node getNode() {
    return node;
  }

  public void setNode( final Node node ) {
    this.node = node;
  }

  public boolean isEntryPoint() {
    return isEntryPoint;
  }

  public void setEntryPoint( final boolean entryPoint ) {
    isEntryPoint = entryPoint;
  }

  public void setReachable( final boolean reachable ) {
    isReachable = reachable;
  }

  public int getAccess() {
    return access;
  }

  public boolean isSynthetic() {
    return ( access & Opcodes.ACC_SYNTHETIC ) == Opcodes.ACC_SYNTHETIC;
  }

  public boolean isAbstract() {
    return ( Opcodes.ACC_ABSTRACT & access) == Opcodes.ACC_ABSTRACT;
  }

  public File getSourceJar() {
    return this.sourceJar;
  }
}
