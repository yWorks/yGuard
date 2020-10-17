package com.yworks.yguard.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Test for inner classes with apply to members = false
 */
@Obfuscation(applyToMembers = false, exclude = true)
public class AnnoTest6KEEP {

  /**
   * The Public bool field 1 keep.
   */
  @Obfuscation(exclude = true)
  public boolean publicBoolField1KEEP;

  /**
   * The Public bool field 1 obfuscate.
   */
  public boolean publicBoolField1OBFUSCATE;

  /**
   * The type Test 6 inner keep.
   */
  @Obfuscation(applyToMembers = false, exclude = true)
  public static class Test6InnerKEEP {

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
     * The type Test 6 inner inner keep.
     */
    @Obfuscation(applyToMembers = false, exclude = true)
    public static class Test6InnerInnerKEEP {

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
   * The type Test 6 inner obfuscate.
   */
  public static class Test6InnerOBFUSCATE {

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
     * The type Test 6 inner inner obfuscate.
     */
    public static class Test6InnerInnerOBFUSCATE {

      /**
       * The Innerinner bool field 1 obfuscate.
       */
      public boolean innerinnerBoolField1OBFUSCATE;
    }
  }

  /**
   * The type Test 6 inner 2 obfuscate.
   */
  @Obfuscation(applyToMembers = false, exclude = false)
  public static class Test6Inner2OBFUSCATE {

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
     * The type Test 6 inner inner 2 obfuscate.
     */
    public static class Test6InnerInner2OBFUSCATE {

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
