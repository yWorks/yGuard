package com.yworks.yshrink.ant.filters;

import com.yworks.yshrink.ant.MethodSection;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;
import com.yworks.yshrink.model.Model;
import org.apache.tools.ant.Project;

import java.util.Collection;

/**
 * The type Serialization filter.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class SerializationFilter extends MethodFilter {

  /**
   * Instantiates a new Serialization filter.
   *
   * @param project the project
   */
  public SerializationFilter( Project project ) {
    super(project);

    MethodSection msWrite = new MethodSection();
    msWrite.setSignature("void writeObject(java.io.ObjectOutputStream)");
    msWrite.setAccess("private");
    addMethodSection(msWrite);

    MethodSection msRead = new MethodSection();
    msRead.setSignature("void readObject(java.io.ObjectInputStream)");
    msRead.setAccess("private");
    addMethodSection(msRead);

  }

  @Override
  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {

    boolean r = false;

    Collection<String> interfaces = cd.getAllImplementedInterfaces(model);
    if (interfaces.contains("java/io/Serializable")) {
      r = true;
    }

    return r && super.isEntryPointMethod(model, cd, md);
  }
}
