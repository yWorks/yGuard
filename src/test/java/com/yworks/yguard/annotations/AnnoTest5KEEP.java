package com.yworks.yguard.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Test for inner classes
 */
@Obfuscation(applyToMembers = true, exclude = true)
public class AnnoTest5KEEP {

  /**
   * The Public bool field 1 keep.
   */
  public boolean publicBoolField1KEEP;

  /**
   * The Public bool field 1 obfuscate.
   */
  @Obfuscation(exclude = false)
  public boolean publicBoolField1OBFUSCATE;

  /**
   * The type Test 5 inner keep.
   */
  @Obfuscation(applyToMembers = true, exclude = true)
  public static class Test5InnerKEEP {

    /**
     * The Inner bool field 1 keep.
     */
    public boolean innerBoolField1KEEP;

    /**
     * The Inner bool field 1 obfuscate.
     */
    @Obfuscation(exclude = false)
    public boolean innerBoolField1OBFUSCATE;

    /**
     * The type Test 5 inner inner keep.
     */
    public static class Test5InnerInnerKEEP {

      /**
       * The Innerinner bool field 1 obfuscate.
       */
      public boolean innerinnerBoolField1OBFUSCATE;

      /**
       * The Innerinner bool field 1 keep.
       */
      @Obfuscation(exclude = true)
      public boolean innerinnerBoolField1KEEP;
    }
  }

  /**
   * The type Test 5 inner obfuscate.
   */
  @Obfuscation(exclude = false)
  public static class Test5InnerOBFUSCATE {

    /**
     * The Inner bool field 1 keep.
     */
    @Obfuscation(exclude = true)
    public boolean innerBoolField1KEEP;

    /**
     * The Inner bool field 1 obfuscate.
     */
    public boolean innerBoolField1OBFUSCATE;

    /**
     * The type Test 5 inner inner obfuscate.
     */
    public static class Test5InnerInnerOBFUSCATE {

      /**
       * The Innerinner bool field 1 obfuscate.
       */
      public boolean innerinnerBoolField1OBFUSCATE;
    }
  }

  /**
   * The type Test 5 inner 2 keep.
   */
  public static class Test5Inner2KEEP {

    /**
     * The Inner bool field 1 keep.
     */
    @Obfuscation(exclude = true)
    public boolean innerBoolField1KEEP;

    /**
     * The Inner bool field 1 obfuscate.
     */
    public boolean innerBoolField1OBFUSCATE;

    /**
     * The type Test 5 inner inner obfuscate.
     */
    public static class Test5InnerInnerOBFUSCATE {

      /**
       * The Innerinner bool field 1 obfuscate.
       */
      public boolean innerinnerBoolField1OBFUSCATE;

      /**
       * The Innerinner bool field 1 keep.
       */
      @Obfuscation(exclude = true)
      public boolean innerinnerBoolField1KEEP;
    }
  }
}
