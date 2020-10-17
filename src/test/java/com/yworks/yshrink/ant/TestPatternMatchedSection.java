package com.yworks.yshrink.ant;

import com.yworks.common.ant.PatternMatchedSection;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * The type Test pattern matched section.
 *
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class TestPatternMatchedSection {

    /**
     * Test is access level.
     */
    @Test
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
