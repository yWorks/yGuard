package com.yworks.yshrink.ant;

import org.objectweb.asm.Opcodes;
import junit.framework.TestCase;
import com.yworks.yguard.common.ant.PatternMatchedSection;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class TestPatternMatchedSection extends TestCase {

  public void testIsAccessLevel() {

    assertTrue(
        PatternMatchedSection.Access.PUBLIC.isAccessLevel(
            Opcodes.ACC_PUBLIC ) );

    assertFalse(
        PatternMatchedSection.Access.PUBLIC.isAccessLevel(
            Opcodes.ACC_PRIVATE ) );

    assertFalse(
        PatternMatchedSection.Access.PUBLIC.isAccessLevel(
            Opcodes.ACC_PROTECTED ) );

    assertFalse(
        PatternMatchedSection.Access.PROTECTED.isAccessLevel(
            Opcodes.ACC_PRIVATE ) );

    assertFalse(
        PatternMatchedSection.Access.PUBLIC.isAccessLevel(
            PatternMatchedSection.Access.FRIENDLY ) );

  }

}
