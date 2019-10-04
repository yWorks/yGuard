package com.yworks.yguard.test.annotations;

import com.yworks.util.annotation.Obfuscation;

/**
 * Test for inner classes with apply to members = false
 */
@Obfuscation(applyToMembers = false, exclude = true)
public class AnnoTest6KEEP {

  @Obfuscation( exclude = true)
  public boolean publicBoolField1KEEP;

  public boolean publicBoolField1OBFUSCATE;

  @Obfuscation(applyToMembers = false, exclude = true)
  public static class Test6InnerKEEP {

    @Obfuscation( exclude = true)
    public boolean innerBoolField1KEEP;

    public boolean innerBoolField1OBFUSCATE;

    @Obfuscation(applyToMembers = false, exclude = true)
    public static class Test6InnerInnerKEEP {

      public boolean innerinnerBoolField1OBFUSCATE;

      @Obfuscation( exclude = true)
      public boolean innerinnerBoolField1KEEP;
    }
  }

  public static class Test6InnerOBFUSCATE {

    @Obfuscation( exclude = true)
    public boolean innerBoolField1KEEP;

    public boolean innerBoolField1OBFUSCATE;

    public static class Test6InnerInnerOBFUSCATE {

      public boolean innerinnerBoolField1OBFUSCATE;
    }
  }

  @Obfuscation(applyToMembers = false, exclude = false)
  public static class Test6Inner2OBFUSCATE {

    @Obfuscation( exclude = true)
    public boolean innerBoolField1KEEP;

    public boolean innerBoolField1OBFUSCATE;

    public static class Test6InnerInner2OBFUSCATE {

      public boolean innerinnerBoolField1OBFUSCATE;

      @Obfuscation( exclude = true)
      public boolean innerinnerBoolField1KEEP;
    }
  }
}
