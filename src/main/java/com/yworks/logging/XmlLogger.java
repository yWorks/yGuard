package com.yworks.logging;

import com.yworks.util.Version;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class XmlLogger extends Logger {

  private PrintWriter pw;

  public XmlLogger( PrintWriter pw ) {
    this.pw = pw;
    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    pw.println("<yshrink version=\""+Version.getVersion()+"\">");
    register();
  }

  public void doLog( String s ) {
    pw.println( "<!-- " + s + " -->" );
  }

  public void doErr( String s ) {
    pw.println( "<!-- ERROR: " );
    pw.println( s );
    pw.println( "-->" );
  }

  public void doErr( String s, Throwable ex ) {
    pw.println( "<!-- ERROR: " );
    pw.println( s );
    ex.printStackTrace( pw );
    pw.println( "-->" );
  }

  public void doWarn( String s ) {
    pw.println( "<!-- WARNING:" + s + " -->" );
  }

  public void doWarnToLog( String s ) {
    pw.println( "<!-- WARNING:" + s + " -->" );
  }

  public void doShrinkLog( String s ) {
    pw.println( s );
  }

  public void close() {
    pw.println("</yshrink>");
    pw.println();
    pw.close();
    unregister();
  }

  public static String replaceSpecialChars( String s ) {

    StringReader reader = new StringReader( s );

    StringBuilder r = new StringBuilder();

    int i;
    try {
      while( (i = reader.read()) != -1 ) {

        char c = (char) i;

        switch( c ) {

          case '>' :
              r.append( "&gt;" );
              break;

          case '<':
              r.append( "&lt;" );
              break;

          default:
            r.append( c );
        }

      }
    } catch ( IOException e ) {
      Logger.err( e.getMessage() );
    }
    return r.toString();
  }

}
