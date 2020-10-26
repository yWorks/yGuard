package com.yworks.yshrink.ant;

import com.yworks.yshrink.core.ClassResolver;
import com.yworks.logging.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Resource cp resolver.
 *
 * @author Sebastian Mueller, yWorks GmbH  (sebastian.mueller@yworks.com)
 */
public class ResourceCpResolver implements ClassResolver {
  private Path resource;
    /**
     * The Url class loader.
     */
    URLClassLoader urlClassLoader;

    /**
     * Instantiates a new Resource cp resolver.
     *
     *
		 * @param resources the resources
     *
		 * @param target    the target
     */
    public ResourceCpResolver(final Path resources, final Task target) {
    this.resource = resources;
    final String[] list = resources.list();
    final List listUrls = new ArrayList();
    for ( int i = 0; i < list.length; i++ ) {
      try {
        final URL url = new File( list[ i ] ).toURL();
        listUrls.add( url );
      } catch ( MalformedURLException mfue ) {
        Logger.err( "Could not resolve resource: " + mfue );
        target.getProject().log( target, "Could not resolve resource: " + mfue, Project.MSG_WARN );
      }
    }
    final URL[] urls = new URL[listUrls.size()];
    listUrls.toArray( urls );
    urlClassLoader = URLClassLoader.newInstance( urls, ClassLoader.getSystemClassLoader() );
  }

  public Class resolve( final String className ) throws ClassNotFoundException {
    try {
      return Class.forName( className, false, urlClassLoader );
    } catch ( NoClassDefFoundError ncdfe ) {
      String message = ncdfe.getMessage();
      if ( message == null || message.equals( className ) ) {
        message = className;
      } else {
        message = message + "[" + className + "]";
      }
      throw new ClassNotFoundException( message, ncdfe );
    } catch ( LinkageError le ) {
      throw new ClassNotFoundException( className, le );
    }
  }

  @Override
  public void close() throws Exception {
    urlClassLoader.close();
  }
}