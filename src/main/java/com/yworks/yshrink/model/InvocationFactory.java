package com.yworks.yshrink.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Invocation factory.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class InvocationFactory {

  private static final InvocationFactory instance = new InvocationFactory();

  /**
   * Gets instance.
   *
   * @return the instance
   */
  protected static InvocationFactory getInstance() {
    return instance;
  }

  private final Map<String, Invocation> invocations = new HashMap<String, Invocation>();

  /**
   * Gets invocation.
   *
   * @param opcode the opcode
   * @param type   the type
   * @param name   the name
   * @param desc   the desc
   * @return the invocation
   */
  protected Invocation getInvocation( final int opcode, final String type, final String name, final String desc ) {
    String key = new StringBuilder(type).append(name).append(desc).append(opcode).toString();
    Invocation val = invocations.get(key);
    if (null == val) {
      val = new Invocation(opcode, type, name, desc);
      invocations.put(key, val);
    }
    return val;
  }

}
