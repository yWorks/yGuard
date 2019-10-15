package com.yworks.yshrink.ant;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import com.yworks.yguard.common.ant.PatternMatchedSection;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Michael Schroeder, yWorks GmbH http://www.yworks.com
 */
public class TestPatternMatchedSection {

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
