package com.yworks.logging;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class Logger {


  public enum ShrinkType { CLASS,METHOD,FIELD }


  private static List<Logger> instances;

  protected void register() {
    if ( null == Logger.instances ) {
      Logger.instances = new ArrayList<Logger>();
    }
    Logger.instances.add( this );
    //System.out.println( "Using logger: " + this.getClass().getName() +" ("+Logger.instances.size()+")");
  }

  protected void unregister() {
    Logger.instances.remove( this );
  }

  public static void log( final String s ) {
    //System.out.println( "logging: "+s );
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doLog( s );
      }
    }
  }

  public static void err( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doErr( s );
      }
    }
  }

  public static void err( final String s, final Throwable ex ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doErr( s, ex );
      }
    }
  }


  public static void warn( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doWarn( s );
      }
    }
  }

  public static void warnToLog( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doWarnToLog( s );
      }
    }
  }

  public static void shrinkLog( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doShrinkLog( s );
      }
    }
  }

  public abstract void doLog( String s );

  public abstract void doErr( String s );

  public abstract void doWarn( String s );

  public abstract void doWarnToLog( String s );

  public abstract void doShrinkLog( String s );

  public abstract void doErr( String s, Throwable ex );

  public abstract void close(); 



}
