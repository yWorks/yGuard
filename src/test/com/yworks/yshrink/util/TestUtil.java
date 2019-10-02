package com.yworks.yshrink.util;

import junit.framework.TestCase;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class TestUtil extends TestCase {



  public void testVerboseToNativeType() {

    String query = "double[][][]";
    String expected = "[[[D";
    String result = Util.verboseToNativeType( query );
    assertEquals( expected, result );

    query = "java.lang.String";
    expected = "Ljava/lang/String;";
    result = Util.verboseToNativeType( query );
    assertEquals( expected, result );

    query = "java.lang.String[]";
    expected = "[Ljava/lang/String;";
    result = Util.verboseToNativeType( query );
    assertEquals( expected, result );

  }

}
