package com.yworks.logging;

import java.util.List;
import java.util.ArrayList;

/**
 * The type Logger.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public abstract class Logger {


    /**
     * The enum Shrink type.
     */
    public enum ShrinkType {
        /**
         * Class shrink type.
         */
        CLASS,
        /**
         * Method shrink type.
         */
        METHOD,
        /**
         * Field shrink type.
         */
        FIELD }


  private static List<Logger> instances;

    /**
     * Register.
     */
    protected void register() {
    if ( null == Logger.instances ) {
      Logger.instances = new ArrayList<Logger>();
    }
    Logger.instances.add( this );
    //System.out.println( "Using logger: " + this.getClass().getName() +" ("+Logger.instances.size()+")");
  }

    /**
     * Unregister.
     */
    protected void unregister() {
    Logger.instances.remove( this );
  }

    /**
     * Log.
     *
     * @param s the s
     */
    public static void log( final String s ) {
    //System.out.println( "logging: "+s );
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doLog( s );
      }
    }
  }

    /**
     * Err.
     *
     * @param s the s
     */
    public static void err( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doErr( s );
      }
    }
  }

    /**
     * Err.
     *
     * @param s  the s
     * @param ex the ex
     */
    public static void err( final String s, final Throwable ex ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doErr( s, ex );
      }
    }
  }


    /**
     * Warn.
     *
     * @param s the s
     */
    public static void warn( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doWarn( s );
      }
    }
  }

    /**
     * Warn to log.
     *
     * @param s the s
     */
    public static void warnToLog( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doWarnToLog( s );
      }
    }
  }

    /**
     * Shrink log.
     *
     * @param s the s
     */
    public static void shrinkLog( final String s ) {
    if ( null != instances ) {
      for ( Logger logger : instances ) {
        logger.doShrinkLog( s );
      }
    }
  }

    /**
     * Do log.
     *
     * @param s the s
     */
    public abstract void doLog( String s );

    /**
     * Do err.
     *
     * @param s the s
     */
    public abstract void doErr( String s );

    /**
     * Do warn.
     *
     * @param s the s
     */
    public abstract void doWarn( String s );

    /**
     * Do warn to log.
     *
     * @param s the s
     */
    public abstract void doWarnToLog( String s );

    /**
     * Do shrink log.
     *
     * @param s the s
     */
    public abstract void doShrinkLog( String s );

    /**
     * Do err.
     *
     * @param s  the s
     * @param ex the ex
     */
    public abstract void doErr( String s, Throwable ex );

    /**
     * Close.
     */
    public abstract void close();



}
