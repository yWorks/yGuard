package com.yworks.yshrink.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class InvocationFactory {

  private static final InvocationFactory instance = new InvocationFactory();

  protected static InvocationFactory getInstance() {
    return instance;
  }

  private Map<String, Invocation> invocations = new HashMap<String, Invocation>();

  protected Invocation getInvocation( final int opcode, final String type, final String name, final String desc ) {
    String key = new StringBuilder( type ).append( name ).append( desc ).append( opcode ).toString();
    Invocation val = invocations.get( key );
    if ( null == val ) {
      val = new Invocation( opcode, type, name, desc );
      invocations.put( key, val );
    }
    return val;
  }

}
