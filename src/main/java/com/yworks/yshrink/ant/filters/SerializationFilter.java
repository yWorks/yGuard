package com.yworks.yshrink.ant.filters;

import org.apache.tools.ant.Project;
import com.yworks.yshrink.ant.MethodSection;
import com.yworks.yshrink.model.Model;
import com.yworks.yshrink.model.ClassDescriptor;
import com.yworks.yshrink.model.MethodDescriptor;

import java.util.Collection;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class SerializationFilter extends MethodFilter {

  public SerializationFilter( Project project ) {
    super( project );

    MethodSection msWrite = new MethodSection();
    msWrite.setSignature( "void writeObject(java.io.ObjectOutputStream)" );
    msWrite.setAccess( "private" );
    addMethodSection( msWrite );

    MethodSection msRead = new MethodSection();
    msRead.setSignature( "void readObject(java.io.ObjectInputStream)" );
    msRead.setAccess( "private" );
    addMethodSection( msRead );

  }

  @Override
  public boolean isEntryPointMethod( final Model model, final ClassDescriptor cd, final MethodDescriptor md ) {

    boolean r = false;

    Collection<String> interfaces = cd.getAllImplementedInterfaces( model );
    if ( interfaces.contains( "java/io/Serializable" ) ) {
      r = true;
    }

    return r && super.isEntryPointMethod( model, cd, md );
  }
}
