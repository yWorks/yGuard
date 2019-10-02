package com.yworks.yshrink.core;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class URLCpResolver implements ClassResolver {

  URLClassLoader urlClassLoader;

  public URLCpResolver( final URL[] urls ) {
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
